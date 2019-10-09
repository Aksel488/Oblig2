////////////////// class DobbeltLenketListe //////////////////////////////


import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;

import static java.lang.System.out;


/*
Oppgaven er levert av følgende studenter:
* Aksel Susegg, s325917
* Sander Saether, s331358
* Runar Sivertsen, s331414
* Jørgen Røkke Bender, s331368
*/


public class DobbeltLenketListe<T> implements Liste<T> {


    /**
     * Node class
     * @param <T>
     */
    private static final class Node<T> {
        private T verdi;                   // nodens verdi
        private Node<T> forrige, neste;    // pekere

        private Node(T verdi, Node<T> forrige, Node<T> neste) {
            this.verdi = verdi;
            this.forrige = forrige;
            this.neste = neste;
        }

        private Node(T verdi) {
            this(verdi, null, null);
        }

    }

    // instansvariabler
    private Node<T> hode;          // peker til den første i listen
    private Node<T> hale;          // peker til den siste i listen
    private int antall;            // antall noder i listen
    private int endringer;         // antall endringer i listen


    public DobbeltLenketListe() {   //Tom liste konstruktør
        hode = null;
        hale = null;

        antall = 0;
        endringer = 0;
    }

    public DobbeltLenketListe(T[] a) {  //Konstruktør

        if (a == null) {
            throw new NullPointerException();
        }

        if (a.length > 0) {
            int i = 0;
            for (; i < a.length; i++) { //finner første ikke null element og lager hode
                if (a[i] != null) {

                    hode = new Node<>(a[i]);
                    antall++;
                    break;
                }
            }

            hale = hode;
            if (hode != null) {     //Lager resten av listen
                i++;
                for (; i < a.length; i++) {
                    if (a[i] != null) {

                        hale.neste = new Node<>(a[i], hale, null);
                        hale = hale.neste;
                        antall++;
                    }
                }
            }
        }
    }

    public Liste<T> subliste(int fra, int til){

        fratilKontroll(antall, fra, til);

        Liste<T> liste = new DobbeltLenketListe<>();
        int lengde = til - fra;

        if (lengde < 1) {
            return liste;
        }

        Node<T> current = finnNode(fra);

        while (lengde > 0) {
            liste.leggInn(current.verdi);
            current = current.neste;
            lengde--;
        }

        return liste;
    }

    //Hjelpemetode
    private void fratilKontroll(int tabLengde, int fra, int til) {
        if (fra < 0 || til > tabLengde) {
            throw new IndexOutOfBoundsException();
        }
        if (fra > til) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int antall() {
        return antall;
    }

    @Override
    public boolean tom() {
        if (hode == null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean leggInn(T verdi) {
        Objects.requireNonNull(verdi);

        Node<T> nyNode = new Node<>(verdi);

        if (hode == null && hale == null && antall == 0) { //Tilfelle 1 : tom liste
            hode = nyNode;
            hale = hode;

            endringer++;
            antall++;
            return true;
        } else {   //Tilfelle 2 : ikke tom liste
            nyNode.forrige = hale;
            hale.neste = nyNode;
            hale = nyNode;

            endringer++;
            antall++;
            return true;
        }
    }

    //Hjelpemetode
    private Node<T> finnNode(int indeks) {

        indeksKontroll(indeks, false);

        Node<T> current;

        if (indeks < antall / 2) { //Søker fra hode
            current = hode;
            for (int i = 0; i < indeks; i++) {
                current = current.neste;
            }
            return current;

        } else { //Søker fra hale
            current = hale;
            for (int i = antall - 1; i > indeks; i--) {
                current = current.forrige;
            }
            return current;
        }
    }

    @Override
    public void leggInn(int indeks, T verdi) {
        // Sjekker om verdien er nullobjekt
        Objects.requireNonNull(verdi, "Verdien kan ikke være null");

        //Sjekker størrelse på indeksen
        if (indeks > antall){
            throw new IndexOutOfBoundsException("Indeks er større enn antall noder");
        } else if (indeks < 0) throw new IndexOutOfBoundsException("Indeksen kan ikke være negativ");

        if (antall == 0 && indeks == 0) {
            hode = hale = new Node<T>(verdi, null, null);
        }
        else if (indeks == antall) {
            hale = new Node<T>(verdi, hale, null);
            hale.forrige.neste = hale;
        } else if (indeks == 0) {
        hode = new Node<T>(verdi, null, hode);
        hode.neste.forrige = hode;
        }
        else {
            Node<T> node = hode;

            for (int i = 0; i < indeks; i++) node = node.neste;{
                node = new Node<T>(verdi, node.forrige, node);
            }
            node.neste.forrige = node.forrige.neste = node;
        }

        antall++;
        endringer++;
    }

    @Override
    public boolean inneholder(T verdi) {
        return indeksTil(verdi) != -1;
      //  throw new NotImplementedException();
    }

    @Override
    public T hent(int indeks) {

        Node<T> current = finnNode(indeks);

        return current.verdi;
    }

    @Override
    public int indeksTil(T verdi) {
        if (verdi == null) return -1;

        Node <T> p=hode;
        for (int i = 0; i< antall; i++, p = p.neste)
        {
            if (p.verdi.equals(verdi)) return i;
        }

        return -1;
    }

    @Override
    public T oppdater(int indeks, T nyverdi) {

        Objects.requireNonNull(nyverdi);

        Node<T> current = finnNode(indeks);

        T gammelVerdi = current.verdi;
        endringer++;

        current.verdi = nyverdi;

        return gammelVerdi;
    }

    @Override
    public boolean fjern(T verdi) {
        if (verdi == null) {
            return false;
        }

        Node<T> current = hode;

        //Første fjernes
        if (verdi.equals(current.verdi)) {
            if (current.neste != null) {
                hode = current.neste;
                hode.forrige = null;
            } else {
                hode = null;
                hale = null;
            }
            antall--;
            endringer++;
            return true;
        }

        //Siste fjernes
        current = hale;
        if (verdi.equals(current.verdi)) {
            hale = current.forrige;
            hale.neste = null;
            antall--;
            endringer++;
            return true;
        }

        //Mellom fjernes
        current = hode.neste;
        for (; current != null; current = current.neste) {
            if (verdi.equals(current.verdi)) {
                current.forrige.neste = current.neste;  //Noden til venstre for current peker på noden til høyre
                current.neste.forrige = current.forrige;//Noden til høyre for current peker på noden til venstre
                antall--;
                endringer++;
                return true;
            }
        }
        return false;
    }

    @Override
    public T fjern(int indeks) {
        indeksKontroll(indeks, false);

        Node<T> current = hode;
        T verdi;

        //Første fjernes
        if (indeks == 0) {
            verdi = current.verdi;

            if (current.neste != null) {
                hode = current.neste;
                hode.forrige = null;
            } else {
                hode = null;
                hale = null;
            }
        }

        //Siste fjernes
        else if (indeks == antall - 1) {
            current = hale;
            verdi = hale.verdi;

            hale = current.forrige;
            hale.neste = null;
        }

        //Mellom fjernes
        else {
            for (int i = 0; i < indeks; i++) {
                current = current.neste;
            }
            verdi = current.verdi;

            current.forrige.neste = current.neste;  //Noden til venstre for current peker på noden til høyre
            current.neste.forrige = current.forrige;//Noden til høyre for current peker på noden til venstre
        }

        antall--;
        endringer++;
        return verdi;
    }

    @Override
    public void nullstill() {
        //Metode 1
        for(Node<T> t = hode; t != null; t = t.neste) {
            t.verdi = null;
            t.forrige = t.neste = null;
        }
        hode = hale = null;
        antall = 0;
        endringer++;
        /*Metode 2 funker men tar lengre tid, velger i dette tilfellet metode 1
        for (Node<T> t = hode; t != null; t=t.neste){
            fjern(0);
        }
         */
    }

    @Override
    public String toString() {

        Node<T> current = hode;
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        if (tom()) {
            sb.append("]");
            return sb.toString();
        } else {
            sb.append(current.verdi);
            current = current.neste;
            while (current != null) {
                sb.append(", ");
                sb.append(current.verdi);
                current = current.neste;
            }
        }
        sb.append("]");

        return sb.toString();
    }

    public String omvendtString() {

        Node<T> current = hale;
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        if (tom()) {
            sb.append("]");
            return sb.toString();
        } else {
            sb.append(current.verdi);
            current = current.forrige;
            while (current != null) {
                sb.append(", ");
                sb.append(current.verdi);
                current = current.forrige;
            }
        }
        sb.append("]");

        return sb.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new DobbeltLenketListeIterator();
    }

    public Iterator<T> iterator(int indeks) {
        indeksKontroll(indeks, false);
        return new DobbeltLenketListeIterator(indeks);
    }

    private class DobbeltLenketListeIterator implements Iterator<T>
    {
        private Node<T> denne; // nåværende node som itereres
        private boolean fjernOK;
        private int iteratorendringer;

        private DobbeltLenketListeIterator(){
            denne = hode;     // p starter på den første i listen
            fjernOK = false;  // blir sann når next() kalles
            iteratorendringer = endringer;  // teller endringer
        }

        private DobbeltLenketListeIterator(int indeks){
            denne = finnNode(indeks);     // p starter på indeks
            fjernOK = false;  // blir sann når next() kalles
            iteratorendringer = endringer;  // teller endringer
        }

        @Override
        public boolean hasNext(){
            return denne != null;
        }

        @Override
        public T next(){
            if (!hasNext()) throw new NoSuchElementException("Ingen verdier!");

            if (endringer != iteratorendringer)
                throw new ConcurrentModificationException("Listen er endret!");

            T tempverdi = denne.verdi;
            denne = denne.neste;

            fjernOK = true;

            return tempverdi;
        }

        @Override
        public void remove(){
            Node<T> p = (denne == null ? hale : denne.forrige);
            if(!fjernOK){
                throw new IllegalStateException("Kan ikke fjerne noe element nå!");
            }
            if(iteratorendringer != endringer){
                throw new ConcurrentModificationException("Listen er endret!");
            }
            fjernOK = false;

            if (p == hode)
            {
                if (antall == 1){ hode = hale = null;}      // kun en verdi i listen
                else{ hode = hode.neste; hode.forrige = null;}  // fjerner den første
            }
            else if (p == hale){ hale = hale.forrige; hale.neste = null;}  // fjerner den siste
            else{
                p.forrige.neste = p.neste;
                p.neste.forrige = p.forrige;    // fjerner p
            }

            antall--;            // en mindre i listen
            iteratorendringer++;
            endringer++;         // en endring

        }

    } // class DobbeltLenketListeIterator

    public static <T> void sorter(Liste<T> liste, Comparator<? super T> c)
    {
        if (liste.tom()) return;
        for (int i = 0; i < liste.antall(); i++) {
            for (int j = 0; j < liste.antall(); j++) {
                if ((c.compare(liste.hent(i), liste.hent(j))) < 0)
                {
                    T tempVerdi = liste.hent(i);
                    liste.oppdater(i,liste.hent(j));
                    liste.oppdater(j,tempVerdi);
                }
            }
        }
    }
} // class DobbeltLenketListe


