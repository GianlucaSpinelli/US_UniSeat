package model.database;

import control.utili.PassowrdEncrypter;
import model.dao.UtenteDAO;
import model.dao.ViolazioneEntityException;
import model.pojo.TipoUtente;
import model.pojo.Utente;

import java.util.ArrayList;
import java.util.List;

public class StubUtenteDAO implements UtenteDAO {

    private ArrayList<Utente> utenti;

    {
        utenti = new ArrayList<>();
    }

    @Override
    public Utente retriveByEmail(String email) {
        for (Utente u : utenti) {
            if (u.getEmail().equals(email))
                return u;
        }
        return null;
    }

    @Override
    public void update(Utente utente) throws ViolazioneEntityException {
        for (Utente u : utenti) {
            if (u.getEmail().equals(utente.getEmail())) {
                u.setNome(utente.getNome());
                u.setCognome(utente.getCognome());
                u.setPassword(utente.getPassword());
                u.setTipoUtente(utente.getTipoUtente());
                break;
            }
        }
    }

    @Override
    public void insert(Utente utente) throws ViolazioneEntityException {
        if (!utenti.contains(utente))
            utenti.add(utente);
        else
            throw new ViolazioneEntityException("Utente già esistente");
    }

    @Override
    public void delete(Utente utente) {
        utenti.remove(utente);
    }

    @Override
    public List<Utente> retriveAll() {
        return utenti;
    }
}
