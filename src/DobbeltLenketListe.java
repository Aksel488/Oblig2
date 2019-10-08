////////////////// class DobbeltLenketListe //////////////////////////////


import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;



public class DobbeltLenketListe<T> implements Liste<T> {


    public static void main(String[] args) {

        Character[] c = {'A','B','C','D','E','F','G','H','I','J'};
        DobbeltLenketListe<Character> liste = new DobbeltLenketListe<>(c);

        System.out.println(liste.fjern(0));
        System.out.println(liste.fjern(2));
        System.out.println(liste.fjern(liste.antall - 1));

        System.out.println(liste.toString());
        System.out.println(liste.omvendtString());
    }

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
        throw new NotImplementedException();
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
        throw new NotImplementedException();
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
        throw new NotImplementedException();
    }

    public Iterator<T> iterator(int indeks) {
        throw new NotImplementedException();
    }

    private class DobbeltLenketListeIterator implements Iterator<T>
    {
        private Node<T> denne;
        private boolean fjernOK;
        private int iteratorendringer;

        private DobbeltLenketListeIterator(){
            throw new NotImplementedException();
        }

        private DobbeltLenketListeIterator(int indeks){
            throw new NotImplementedException();
        }

        @Override
        public boolean hasNext(){
            throw new NotImplementedException();
        }

        @Override
        public T next(){
            throw new NotImplementedException();
        }

        @Override
        public void remove(){
            throw new NotImplementedException();
        }

    } // class DobbeltLenketListeIterator

    public static <T> void sorter(Liste<T> liste, Comparator<? super T> c) {
        throw new NotImplementedException();
    }

} // class DobbeltLenketListe


