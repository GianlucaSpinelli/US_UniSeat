package control.comuni;

import com.google.gson.Gson;
import control.utili.SessionManager;
import model.dao.UtenteDAO;
import model.database.DBUtenteDAO;
import model.pojo.TipoUtente;
import model.pojo.Utente;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Questa servlet permette di prelevare tutti gli utenti dal sistema
 * @author Capozzoli Lorenzo
 * @version 0.1
 */
@WebServlet("/PrelevaUtenteServlet")
public class PrelevaUtentiServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        getServletContext().setAttribute(UTENTE_DAO_PARAM, DBUtenteDAO.getInstance());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Utente u = SessionManager.getUtente(session);
        if (u == null || !u.getTipoUtente().equals(TipoUtente.ADMIN)) { // se non è admin o non è loggato
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Non hai i permessi per accedere a questa risorsa");
            return;
        }

        UtenteDAO utenteDAO = (UtenteDAO) req.getServletContext().getAttribute(UTENTE_DAO_PARAM);
        List<Utente> utenti = utenteDAO.retriveAll();
        Gson gson = new Gson();
        String jsonString = gson.toJson(utenti);
        try (PrintWriter pw = resp.getWriter()) {
            resp.setStatus(HttpServletResponse.SC_OK);
            pw.print(jsonString);
        }
    }

    public static final String UTENTE_DAO_PARAM = "PrelevaUtenteServlet.UtenteDAO";
}
