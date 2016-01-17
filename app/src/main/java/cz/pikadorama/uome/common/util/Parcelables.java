package cz.pikadorama.uome.common.util;

import java.util.ArrayList;
import java.util.List;

import cz.pikadorama.uome.model.Person;
import cz.pikadorama.uome.model.Transaction;
import cz.pikadorama.uome.model.parcelable.ParcelablePerson;
import cz.pikadorama.uome.model.parcelable.ParcelableTransaction;

public class Parcelables {

    private Parcelables() {}

    public static List<Person> toPersons(List<ParcelablePerson> parcelables) {
        List<Person> persons = new ArrayList<>(parcelables.size());
        for (ParcelablePerson parcelable : parcelables) {
            persons.add(parcelable.getPerson());
        }
        return persons;
    }

    public static ArrayList<ParcelablePerson> fromPersons(List<Person> persons) {
        ArrayList<ParcelablePerson> parcelables = new ArrayList<>(persons.size());
        for (Person person : persons) {
            parcelables.add(new ParcelablePerson(person));
        }
        return parcelables;
    }

    public static List<Transaction> toTransactions(List<ParcelableTransaction> parcelables) {
        List<Transaction> transactions = new ArrayList<>(parcelables.size());
        for (ParcelableTransaction parcelableTransaction : parcelables) {
            transactions.add(parcelableTransaction.getTransaction());
        }
        return transactions;
    }

    public static ArrayList<ParcelableTransaction> fromTransactions(List<Transaction> transactions) {
        ArrayList<ParcelableTransaction> parcelables = new ArrayList<>(transactions.size());
        for (Transaction transaction : transactions) {
            parcelables.add(new ParcelableTransaction(transaction));
        }
        return parcelables;
    }

}
