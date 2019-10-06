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

        Liste<Integer> liste = new DobbeltLenketListe<>();
        liste.leggInn(1);
        liste.leggInn(2);
        liste.leggInn(3);

        System.out.println(liste.toString());
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
        throw new NotImplementedException();
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

    @Override
    public void leggInn(int indeks, T verdi) {
        Objects.requireNonNull(verdi);

        if (indeks < 0) throw new IndexOutOfBoundsException("Indeks er negativ");
        else if (indeks > antall)
            throw new IndexOutOfBoundsException("Kan ikke ha større indeks enn antall noder");

        if (antall == 0 && indeks == 0) hode = hale = new Node<T>(verdi, null, null);

        //Verdien skal legges først
        else if (indeks == 0) {
            hode = new Node<T>(verdi, null, hode);
            hode.neste.forrige = hode;
        }
        //Verdien skal legges sist
        else if (indeks == antall) {
            hale = new Node<T>(verdi, hale, null);
            hale.forrige.neste = hale;
        }
        //Verdien legges i midten
        else {
            Node<T> node = hode;
            for (int i = 0; i < indeks; i++) {
                node = node.neste;
            }
            node = new Node<T>(verdi, node.forrige, node);
            node.neste.forrige = node.forrige.neste = node;
        }
        antall++;
        endringer++;
    }

    @Override
    public boolean inneholder(T verdi) {
        throw new NotImplementedException();
    }

    @Override
    public T hent(int indeks) {
        throw new NotImplementedException();
    }

    @Override
    public int indeksTil(T verdi) {
        throw new NotImplementedException();
    }

    @Override
    public T oppdater(int indeks, T nyverdi) {
        throw new NotImplementedException();
    }

    @Override
    public boolean fjern(T verdi) {
        if (verdi == null) return false;

        Node<T> gjeldendeNode = hode;

        while (gjeldendeNode != null) {
            if (gjeldendeNode.verdi.equals(verdi)) {
                break;
            }

            gjeldendeNode = gjeldendeNode.neste;
        }

        if (gjeldendeNode == null) return false;

        if (gjeldendeNode == hode) { // Første node
            hode = hode.neste;

            if (hode != null) {
                hode.forrige = null;
            } else {
                hale = null;
            }
        } else if (gjeldendeNode == hale) { // Siste node
            hale = hale.forrige;
            hale.neste = null;
        } else {
            gjeldendeNode.forrige.neste = gjeldendeNode.neste;
            gjeldendeNode.neste.forrige = gjeldendeNode.forrige;
        }

        gjeldendeNode.verdi = null;
        gjeldendeNode.forrige = gjeldendeNode.neste = null;

        antall--;
        endringer++;

        return true;
    }

    private Node<T> finnNode(int indeks) {
        Node<T> returnNode;

        if (indeks < antall / 2) {
            returnNode = hode;
            for (int i = 0; i < indeks; i++) returnNode = returnNode.neste;
        } else {
            returnNode = hale;
            for (int i = antall - 1; i > indeks; i--) returnNode = returnNode.forrige;
        }

        return returnNode;
    }

    private void indeksKontroll(int indeks) {
        if (indeks < 0) {
            throw new IndexOutOfBoundsException("Indeks " + indeks + " er negativ!");
        } else if (indeks >= antall) {
            throw new IndexOutOfBoundsException("Indeks " + indeks + " >= antall(" + antall + ") noder!");
        }
    }

    @Override
    public T fjern(int indeks) {
        indeksKontroll(indeks);
        Node<T> temp;

        if (indeks == 0) { // Første node
            temp = hode;
            hode = hode.neste;
            hode.forrige = null;
        } else if (indeks == antall - 1) { // Siste node
            temp = hale;
            hale = hale.forrige;
            hale.neste = null;
        } else {
            Node<T> p = finnNode(indeks - 1);

            temp = p.neste;

            p.neste = p.neste.neste;
            p.neste.forrige = p;
        }


        antall--;
        endringer++;
        return temp.verdi;
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


