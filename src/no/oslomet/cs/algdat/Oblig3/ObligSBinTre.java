package no.oslomet.cs.algdat.Oblig3;

////////////////// ObligSBinTre /////////////////////////////////

import java.lang.reflect.Array;
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
    public String toString(){ return ""+verdi ; }

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
    if (antall == 0 || verdi == null || rot == null) return false;
    endringer++;

    Node current = rot;

    if(comp.compare(verdi, rot.verdi) == 0 && antall == 1){
      rot = null;
      antall--;
      return true;
    }else if(comp.compare(verdi,(T)rot.verdi) == 0){
      Node venstre = rot.venstre;
      if(rot.høyre != null) {
        rot = rot.høyre;
        leggTilNode(venstre);
      }else{
        rot = venstre;
        rot.forelder = null;
      }
      antall--;
      return true;
    }

    Boolean harSlettet = false;
    while (current != null) {
      int cmp = comp.compare(verdi, (T) current.verdi);
      //Om verdiene er like har vi funnet verdien som skal fjernes
      if (cmp == 0) {
        Node venstre = current.venstre;
        //Sjekker om current er høyre barn til sin forelder
        Boolean erHøyreBarn = true;
        if (current.forelder.venstre != null && current.forelder.venstre == current) {
          erHøyreBarn = false;
        }
        //Sjekeker om current har høyre barn
        Boolean harHøyreBarn = true;
        if (current.høyre == null) {
          harHøyreBarn = false;
        }
        //Sjekker kombinasjoner av harHøyrebarn og erHøyrebarn
        if (erHøyreBarn && harHøyreBarn) {
          current.forelder.høyre = current.høyre;
          current.høyre.forelder = current.forelder;
          leggTilNode(current.venstre);
          antall--;
          return true;
        } else if (!erHøyreBarn && harHøyreBarn) {
          current.forelder.venstre = current.høyre;
          current.høyre.forelder = current.forelder;
          leggTilNode(current.venstre);
          antall--;
          return true;
        } else if (current.venstre == null && !harHøyreBarn && erHøyreBarn) {
          current.forelder.høyre = null;
          antall--;
          return true;
        } else if (current.venstre == null && !harHøyreBarn && !erHøyreBarn) {
          current.forelder.venstre = null;
          antall--;
          return true;
        } else if (erHøyreBarn && !harHøyreBarn) {
          current.forelder.høyre = current.venstre;
          current.venstre.forelder = current.forelder;
          antall--;
          return true;
        } else if (!erHøyreBarn && !harHøyreBarn) {
          current.forelder.venstre = current.venstre;
          current.venstre.forelder = current.forelder;
          antall--;
          return true;
        }
        harSlettet = true;
      }

      //Sjekker om verdien er mindre en 0, da vet vi at den er til venstre for current ellers er den til høyre
      if(cmp < 0){
        current = current.venstre;
      }else{
        current = current.høyre;
      }
    }

    if(harSlettet) antall--;
    return harSlettet;
  }
  
  public int fjernAlle(T verdi) {
    int antall = 0;
    while(fjern(verdi)){
      antall++;
    }
    return antall;
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
  public void nullstill() {
    if (rot == null) return;

    ArrayList<Node> list = new ArrayList();
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
      list.add(current);
      current = current.venstre;
    }
    for(Node node: list){
      node.forelder = null;
      node.venstre = null;
      node.høyre = null;
      node.verdi = null;
    }
    antall = 0;
    endringer++;
    rot = null;
  }
  
  private static <T> Node<T> nesteInorden(Node<T> p)
  {
    if(p.høyre != null){
      p = p.høyre;
      while(p.venstre != null){
        p = p.venstre;
      }
      return p;
    } else{
      Node<T> parentNode = p.forelder;
      while(parentNode != null && !(p == parentNode.venstre)){
        p = parentNode;
        parentNode = p.forelder;
      }
      return parentNode;
    }
  }
  
  @Override
  public String toString()
  {
    if(rot == null) return "[]";

    StringBuilder streng = new StringBuilder();
    streng.append("[");

    Node<T> currentNode = rot;
    while(currentNode.venstre != null){
      currentNode = currentNode.venstre;
    }
    streng.append(currentNode.verdi);

    while(nesteInorden(currentNode) != null){
      currentNode = nesteInorden(currentNode);
      streng.append(",").append(" ").append(currentNode.verdi);
    }

    streng.append("]");

    return streng.toString();
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
  
  public String høyreGren() {
    ArrayList<T> arrayList = new ArrayList();
    Node current = rot;
    if(current != null){
      arrayList.add((T)current.verdi);
    }
    Boolean høyreBladnode = false;
    while (!høyreBladnode && current != null){
      if(current.høyre != null){
        current = current.høyre;
      }else if(current.høyre == null && current.venstre != null){
        current = current.venstre;
      }else if (current.høyre == null && current.venstre == null){
        høyreBladnode = true;
        break;
      }
      arrayList.add((T)current.verdi);
    }
    return arrayList.toString();
  }
  
  public String lengstGren() {
    Node current = rot;
    ArrayList arrayList = new ArrayList();
    while (current != null){
      arrayList.add(current.verdi);
      if(nodeHøyde(current.venstre) >= nodeHøyde(current.høyre)){
        current = current.venstre;
      }else{
        current= current.høyre;
      }
    }
    return arrayList.toString();
  }

  //Rekursiv methode for å finne høyden til fra en node til bunden av treet
  public int nodeHøyde(Node node){
    if(node == null) return 0;
    return (1 + Math.max(nodeHøyde(node.venstre), nodeHøyde(node.høyre)));
  }
  
  public String[] grener() {
    ArrayList<Node> bladnodeListe = finnBladnoder(new ArrayList(), rot);
    String[] grener = new String[bladnodeListe.size()];

    int i = 0;
    for(Node node : bladnodeListe){
      ArrayList<T> liste = new ArrayList();
      while(node != null){
        liste.add((T)node.verdi);
        node = node.forelder;
      }
      //Swaper rekkefølgen på arrayet for å få riktig
      for(int j = 0; j < liste.size()/2; j++){
        T temp = liste.get(j);
        liste.set(j, liste.get(liste.size()-1-j));
        liste.set(liste.size()-1-j, temp);
      }
      grener[i++] = liste.toString();
    }
    return grener;
  }

  public ArrayList<Node> finnBladnoder(ArrayList bladnodeListe , Node current){
    if(current == null) return bladnodeListe;
    if(current.venstre == null && current.høyre == null) bladnodeListe.add(current);
    else {
      finnBladnoder(bladnodeListe, current.venstre);
      finnBladnoder(bladnodeListe, current.høyre);
    }
    return bladnodeListe;
  }
  
  public String bladnodeverdier()
  {
    ArrayList ff = new ArrayList();
    Node current = rot;
    finnBladnoder(ff, current);
    return ff.toString();
  }
  
  public String postString(){
    if (rot == null) return "[]";
    Stack<Node> stack1 = new Stack<>();
    Stack<Node> stack2 = new Stack<>();
    Node<T> current = rot;

    //rot til første stack
    stack1.push(current);

    //kjøre løkke så lenge stack1 ikke er tom
    while (!stack1.isEmpty()){
      //Popper fra stack1, pusher til stack2
      Node temp = stack1.pop();
      stack2.push(temp);

      //pushe h&v barn fra temp(som er fjernet)til stack1
      if(temp.venstre != null){
        stack1.push(temp.venstre);
      }
      if(temp.høyre !=null){
        stack1.push(temp.høyre);
      }
    }
    //for riktig rekkefølge
    while (!stack2.isEmpty()) {
      Node temp = stack2.pop();
      stack1.push(temp);
    }

    return stack1.toString();
  }


  public void leggTilNode(Node node){
    if(node == null) return;
    Node current = rot;
    Node currentForelder = rot;
    while (current != null){
      currentForelder = current;
      int cmp = comp.compare((T)node.verdi, (T)current.verdi);
      if(cmp < 0){
        current = current.venstre;
      }else{
        current = current.høyre;
      }
    }
    int cmp = comp.compare((T)node.verdi, (T) currentForelder.verdi);
    if(cmp < 0){
      currentForelder.venstre = node;
      node.forelder = currentForelder;
    }else{
      currentForelder.høyre = node;
      node.forelder = currentForelder;
    }
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
      //tomt tre -> p endres ikke
        if (p == null){
            return;
        }
        if (p!=null){
            while (p.venstre!=null || p.høyre!=null){
                if (p.venstre !=null) {
                    p=p.venstre;
                }
                else {
                    p = p.høyre;
                }
            }
        }
    }
    
    @Override
    public boolean hasNext()
    {
      return p != null;  // Denne skal ikke endres!
    }
    
    @Override
    public T next()
    {
      if(p == null){
          throw new NoSuchElementException("tomt for noder");
    }

      ArrayList<Node> bladnoder = finnBladnoder(new ArrayList(), p);

      if(q == null) p = bladnoder.get(0);
      else{
        for(int i = 1; i < bladnoder.size()-1; i++){
          if(bladnoder.get(i).equals(q)){
            p = bladnoder.get(i+1);
            break;
          }
        }
      }

      if(p.equals(q)){
        removeOK = false;
        throw new NoSuchElementException("tom for noder");
      }

      q = p;

      removeOK =true;

      return p.verdi;
    }
    
    @Override
    public void remove()
    {
        if(!removeOK){
            throw new IllegalStateException();
        }
        Node<T>forelder=q.forelder;

        if(forelder!=null) {
            if (q == q.forelder.venstre) { //fjerne peker til q fra v
                q.forelder.venstre = null;
            } else {
                q.forelder.høyre = null; //fjerner fra høyre
            }
        }
        else{
            rot =null;
        }

        removeOK =false;
        antall--;
        iteratorendringer++;
        endringer++;
    }

  } // BladnodeIterator

  public static void main (String[]args ){
 /*   ObligSBinTre<Character> tre = new ObligSBinTre<>(Comparator.naturalOrder());
    char[] verdier = "IATBHJCRSOFELKGDMPQN".toCharArray();
    for (char c: verdier) tre.leggInn(c);

    System.out.println(tre.bladnodeverdier());
    System.out.println(tre.postString());*/

    int [] a = {4,7,2,9,4,10,8,7,4,6};
    ObligSBinTre<Integer> tre = new ObligSBinTre<>(Comparator.naturalOrder());
  /*  for(int verdi: a) {
      tre.leggInn(verdi);
    }
    System.out.println(tre.postString());*/
    for(Integer k :tre) System.out.print(k+" ");
  }

} // ObligSBinTre
