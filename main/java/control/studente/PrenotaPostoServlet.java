package control.studente;

import control.utili.DisponibilitaManager;
import control.utili.SessionManager;
import model.dao.AulaDAO;
import model.dao.EdificioDAO;
import model.dao.PrenotazioneDAO;
import model.database.DBAulaDAO;
import model.database.DBEdificioDAO;
import model.database.DBPrenotazioneDAO;
import model.pojo.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Time;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Questa classe si occupa di gestire le prenotazione dei posti effettuate dagli studenti.
 *
 * @author De Caro Antonio
 * @version 0.1
 * */
@WebServlet("/PrenotaPostoServlet")
public class PrenotaPostoServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        getServletContext().setAttribute(PRENOTAZIONE_DAO, DBPrenotazioneDAO.getInstance());
        getServletContext().setAttribute(AULA_DAO, DBAulaDAO.getInstance());
        getServletContext().setAttribute(EDIFICIO_DAO, DBEdificioDAO.getInstance());
        getServletContext().setAttribute(CLOCK, Clock.systemDefaultZone());
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
       doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        HttpSession session = req.getSession();
        Utente utente = SessionManager.getUtente(session);
        PrintWriter printWriter = resp.getWriter();
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        if (session.isNew() || utente == null) {
            printWriter.print("Utente non loggato");
            SessionManager.setError(session, "Utente non loggato");
//            resp.sendRedirect(req.getContextPath() + "/_comuni/login.jsp");
            return;
        }

        if (!utente.getTipoUtente().equals(TipoUtente.STUDENTE)) {
            final String ERROR = "Non hai i permessi per accedere a questa funzionalit&agrave;";
            printWriter.print(ERROR);
            SessionManager.setError(session, ERROR);
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, ERROR);
//            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        EdificioDAO edificioDAO = (EdificioDAO) req.getServletContext().getAttribute(EDIFICIO_DAO);
        AulaDAO aulaDAO = (AulaDAO) req.getServletContext().getAttribute(AULA_DAO);
        PrenotazioneDAO prenotazioneDAO = (PrenotazioneDAO) req.getServletContext().getAttribute(PRENOTAZIONE_DAO);

        // controllo campi
        Edificio edificio;
        Aula aula;
        int durata;
        try {
            edificio = parseEdificio(req.getParameter("edificio"), edificioDAO);
            aula = parseAula(req.getParameter("aula"), edificio, aulaDAO);
            durata = parseDurata(req.getParameter("durata"));
        } catch (IllegalArgumentException e) {
            printWriter.print(e.getMessage());
            SessionManager.setError(session, e.getMessage());
//            resp.sendRedirect(req.getContextPath() + "/_comuni/aule.jsp");
            return;
        }
        // fine controllo campi

        Clock clock = (Clock) req.getServletContext().getAttribute(CLOCK);
        Date data = Date.valueOf(LocalDate.now(clock));
        Time oraInizio = Time.valueOf(LocalTime.now(clock));
        Time oraFine = Time.valueOf(oraInizio.toLocalTime().plusMinutes(durata));

        // controllo se l'utente non ha già altre prenotazioni attive.
        List<Prenotazione> prenotazioniUtente = prenotazioneDAO.retriveByUtente(utente);
        for (Prenotazione p : prenotazioniUtente) {
            if (p.getData().equals(data) && p.getOraFine().after(oraInizio)) {
                final String ERROR = "Hai gi&agrave; effettuato una prenotazione";
                printWriter.print(ERROR);
                SessionManager.setError(session, ERROR);
//                resp.sendRedirect(req.getContextPath() + "/_comuni/aule.jsp");
                return;
            }
        }

        DisponibilitaManager disponibilita = new DisponibilitaManager(aula, prenotazioneDAO);
        boolean changed = false;
        while (durata > 0 && !disponibilita.isPostoDisponibile(data, oraInizio, oraFine)) {
            changed = true;
            durata -= 30; // sottraggo 30 minuti fin quando non riesco ad ottenere un orario disponibile.
            oraFine = Time.valueOf(oraInizio.toLocalTime().plusMinutes(durata));
        }

        if (durata == 0) {
            final String ERROR = "Aula non disponibile";
            printWriter.print(ERROR);
            SessionManager.setError(session, ERROR);
//            resp.sendRedirect(req.getContextPath() + "/_comuni/aule.jsp?edificio=" + edificio.getNome());
            return;
        }

        if (changed) {
            final String ERROR = String.format("L'aula è disponibile fino alle %s", oraFine.toString());
            printWriter.print(ERROR);
            SessionManager.setError(session, ERROR);
//            resp.sendRedirect(req.getContextPath() + "/_comuni/aule.jsp?edificio=" + edificio.getNome());
            return;
        }

        Prenotazione p = new Prenotazione();
        p.setData(data);
        p.setOraInizio(oraInizio);
        p.setOraFine(oraFine);
        p.setAula(aula);
        p.setUtente(utente);
        p.setTipoPrenotazione(TipoPrenotazione.POSTO);
        prenotazioneDAO.insert(p);
        aula.setPostiOccupati(aula.getPostiOccupati() + 1); // aggiorno aula
        aulaDAO.update(aula);

        resp.setStatus(HttpServletResponse.SC_OK);
        printWriter.print("Prenotazione effettuata con successo");
//        resp.sendRedirect(req.getContextPath() + "/index.jsp");
    }

    private int parseDurata(String param) {
        if (param == null || param.strip().equals("") || !param.matches("^[+\\-]?[0-9]+$")) {
            throw new IllegalArgumentException("Durata non valida");
        }

        int durata = Integer.parseInt(param);
        if (durata <= 0 || (durata % 30) != 0) {
            throw new IllegalArgumentException("Durata non valida");
        }
        return durata;
    }

    private Aula parseAula(String parameter, Edificio edificio, AulaDAO aulaDAO) throws IllegalArgumentException {
        if (parameter == null || parameter.equals("")) {
            throw new IllegalArgumentException("Aula non valida");
        }
        Aula aula = aulaDAO.retriveById(Integer.parseInt(parameter));
        if (aula == null || !aula.getEdificio().getNome().equals(edificio.getNome())) {
            throw new IllegalArgumentException("Aula non valida");
        }
        return aula;
    }

    private Edificio parseEdificio(String param, EdificioDAO edificioDAO) throws IllegalArgumentException {
        if (param == null || param.strip().equals("")) {
            throw new IllegalArgumentException("Edificio non valido");
        }
        Edificio edificio = edificioDAO.retriveByName(param);
        if (edificio == null) {
            throw new IllegalArgumentException("Edificio non valido");
        }
        return edificio;
    }

    static final String PRENOTAZIONE_DAO = "PrenotaPostoServlet.PrenotazioneDAO";
    static final String AULA_DAO = "PrenotaPostoServlet.AulaDAO";
    static final String EDIFICIO_DAO = "PrenotaPostoServlet.EdificioDAO";
    static final String CLOCK = "PrenotaPostoServlet.Clock";
}
