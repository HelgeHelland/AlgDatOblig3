package no.oslomet.cs.algdat.Oblig3;

////////////////// ObligSBinTre /////////////////////////////////

import java.util.*;

public class ObligSBinTre<T> implements Beholder<T>
{
  private static final class Node<T>   // en indre nodeklasse
  {
    private T verdi;                   // nodens verdi
    private Node<T> venstre, høyre;    // venstre og høyre barn
    private Node<T> forelder;          // forelder

    // konstruktør
    private Node(T verdi, Node<T> v, Node<T> h, Node<T> forelder)
    {
      this.verdi = verdi;
      venstre = v; høyre = h;
      this.forelder = forelder;
    }

    private Node(T verdi, Node<T> forelder)  // konstruktør
    {
      this(verdi, null, null, forelder);
    }

    @Override
    public String toString(){ throw new UnsupportedOperationException("Ikke kodet ennå!"); }

  } // class Node

  private Node<T> rot;                            // peker til rotnoden
  private int antall;                             // antall noder
  private int endringer;                          // antall endringer

  private final Comparator<? super T> comp;       // komparator

  public ObligSBinTre(Comparator<? super T> c)    // konstruktør
  {
    rot = null;
    antall = 0;
    comp = c;
  }
  
  @Override
  public boolean leggInn(T verdi)
  {
    Objects.requireNonNull(verdi, "Verdi som legges inn kan ikke være null");

    Node<T> currentNode = rot;
    Node<T> parentNode = null;
    int cmp = 0;

    while(currentNode != null){
      parentNode = currentNode;
      cmp = comp.compare(verdi, currentNode.verdi);
      currentNode = cmp < 0 ? currentNode.venstre : currentNode.høyre;
    }

    currentNode = new Node<>(verdi, parentNode);

    if(parentNode == null){
      rot = currentNode;
    } else if(cmp < 0){
      parentNode.venstre = currentNode;
    } else{
      parentNode.høyre = currentNode;
    }

    antall++;
    endringer++;
    return true;
  }

  @Override
  public boolean inneholder(T verdi)
  {
    if (verdi == null) return false;

    Node<T> p = rot;

    while (p != null)
    {
      int cmp = comp.compare(verdi, p.verdi);
      if (cmp < 0) p = p.venstre;
      else if (cmp > 0) p = p.høyre;
      else return true;
    }

    return false;
  }
  
  @Override
  public boolean fjern(T verdi){
    if (antall == 0 || verdi == null) return false;

    Node current = rot;

    if(comp.compare(verdi, (T) rot.verdi) == 0 && antall == 1){
      rot = null;
      return true;
    }else if(comp.compare(verdi,(T)rot.verdi) == 0){
      rot.høyre.venstre = rot.venstre;
      rot.høyre = rot;
      return true;
    }

    while(current != null){
      int cmp = comp.compare(verdi, (T)current.verdi);
      if(cmp == 0){//verdiene er like
        if(current.forelder.venstre != null && current.forelder.venstre.equals(current)){
          current.forelder.venstre = current.venstre;
          current.venstre.forelder = current.forelder;
        }else{
          current.forelder.høyre = current.høyre;
          current.høyre.forelder = current.forelder;
        }
        return true;
      }else if(cmp == -1){//Verdien er større en current.verdi
        current = current.høyre;
      }else{
        current = current.venstre;
      }
    }
    return false;
  }
  
  public int fjernAlle(T verdi)
  {
     throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  @Override
  public int antall()
  {
    return antall;
  }
  
  public int antall(T verdi)
  {
    if(verdi == null){
      return 0;
    }

    Node<T> currentNode = rot;

    int counter = 0;

    while(currentNode != null){
      int cmp = comp.compare(verdi, currentNode.verdi);
      currentNode = cmp < 0 ? currentNode.venstre : currentNode.høyre;
      if(cmp == 0){
        counter++;
      }
    }

    return counter;
  }
  
  @Override
  public boolean tom()
  {
    return antall == 0;
  }
  
  @Override
  public void nullstill()
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  private static <T> Node<T> nesteInorden(Node<T> p)
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  @Override
  public String toString()
  {
//    throw new UnsupportedOperationException("Ikke kodet ennå!");
    return "Hei";
  }
  
  public String omvendtString(){
    if (rot == null) return "[]";

    ArrayList<T> omvendtString = new ArrayList<>();
    Stack<Node> stack = new Stack<Node>();
    Node current = rot;

    while (current != null || stack.size() > 0)
    {

      while (current !=  null)
      {
        stack.push(current);
        current = current.høyre;
      }

      current = stack.pop();

      omvendtString.add((T)current.verdi);

      current = current.venstre;
    }
    return omvendtString.toString();
  }
  
  public String høyreGren()
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  public String lengstGren()
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  public String[] grener()
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  public String bladnodeverdier()
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  public String postString()
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  @Override
  public Iterator<T> iterator()
  {
    return new BladnodeIterator();
  }
  
  private class BladnodeIterator implements Iterator<T>
  {
    private Node<T> p = rot, q = null;
    private boolean removeOK = false;
    private int iteratorendringer = endringer;
    
    private BladnodeIterator()  // konstruktør
    {
      throw new UnsupportedOperationException("Ikke kodet ennå!");
    }
    
    @Override
    public boolean hasNext()
    {
      return p != null;  // Denne skal ikke endres!
    }
    
    @Override
    public T next()
    {
      throw new UnsupportedOperationException("Ikke kodet ennå!");
    }
    
    @Override
    public void remove()
    {
      throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

  } // BladnodeIterator
} // ObligSBinTre
