package model.database;

import control.utili.PassowrdEncrypter;
import model.dao.PrenotazioneDAO;
import model.dao.UtenteDAO;
import model.dao.ViolazioneEntityException;
import model.pojo.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StubPrenotazioneDAO implements PrenotazioneDAO {

    private ArrayList<Prenotazione> prenotazioni;
    {
        prenotazioni = new ArrayList<>();
        Date d = new Date(Calendar.getInstance().getTime().getTime());
        prenotazioni.add(new Prenotazione(1,d, new Time(14), new Time(16),
                TipoPrenotazione.POSTO, new Aula("P3", 20,
                200, "sd", new Edificio("F3")),
                new Utente("a.decaro@studenti.unisa.it", "Antonio", "De Caro",
                        "Antonio2", TipoUtente.STUDENTE)));
        prenotazioni.get(0).getAula().setId(1);
        prenotazioni.add(new Prenotazione(2,d, new Time(14), new Time(16),
                TipoPrenotazione.POSTO, new Aula("P4", 20, 200, "sd", new Edificio("F3")),
                new Utente("c.gravino@studenti.unisa.it", "Carmine", "Gravino",
                        "Gravino1", TipoUtente.DOCENTE)));
        prenotazioni.get(1).getAula().setId(2);
    }

    @Override
    public Prenotazione retriveById(int id) throws IllegalArgumentException {
        if (id < 0)
            throw new IllegalArgumentException(String.format("L'id %d non è valido.", id));

        for (Prenotazione p : prenotazioni) {
            if (p.getId() == id)
                return p;
        }
        return null;
    }

    @Override
    public List<Prenotazione> retriveByData(Date data) throws IllegalArgumentException {

//        if (data.after(Date.valueOf(LocalDate.now()))) // controlla la precondizione
//            throw new IllegalArgumentException(String.format("La data %s ancora deve avvenire", data.toString()));

        List<Prenotazione> ret = new ArrayList<>();
        for (Prenotazione p : prenotazioni) {
            if (p.getData().toString().equals(data.toString())) {
                ret.add(p);
            }
        }
        return ret;
    }

    @Override
    public List<Prenotazione> retriveByDataOra(Date data, Time ora) throws IllegalArgumentException {
        List<Prenotazione> ret = new ArrayList<>();
        for (Prenotazione p : prenotazioni) {
            if (p.getData().equals(data) && p.getOraInizio().equals(ora))
                ret.add(p);
        }
        return ret;
    }

    @Override
    public List<Prenotazione> retriveByUtente(Utente utente) throws ViolazioneEntityException {
        List<Prenotazione> ret = new ArrayList<>();
        for (Prenotazione p : prenotazioni) {
            if (p.getUtente().equals(utente))
                ret.add(p);
        }
        return ret;
    }

    @Override
    public List<Prenotazione> retriveByAula(Aula aula) throws ViolazioneEntityException {
        List<Prenotazione> ret = new ArrayList<>();
        for (Prenotazione p : prenotazioni) {
            if (p.getAula().equals(aula))
                ret.add(p);
        }
        return ret;
    }

    @Override
    public void insert(Prenotazione prenotazione) throws ViolazioneEntityException {
        prenotazioni.add(prenotazione);
    }

    @Override
    public void delete(Prenotazione prenotazione) {
        prenotazioni.remove(prenotazione);
    }

    @Override
    public List<Prenotazione> retriveAll() {
        return prenotazioni;
    }
}
