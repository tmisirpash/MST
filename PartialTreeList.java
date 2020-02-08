package app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.Arc;
import structures.Graph;
import structures.PartialTree;
import structures.Vertex;

/**
 * Stores partial trees in a circular linked list
 * 
 */
public class PartialTreeList implements Iterable<PartialTree> {
    
	/**
	 * Inner class - to build the partial tree circular linked list 
	 * 
	 */
	public static class Node {
		/**
		 * Partial tree
		 */
		public PartialTree tree;
		
		/**
		 * Next node in linked list
		 */
		public Node next;
		
		/**
		 * Initializes this node by setting the tree part to the given tree,
		 * and setting next part to null
		 * 
		 * @param tree Partial tree
		 */
		public Node(PartialTree tree) {
			this.tree = tree;
			next = null;
		}
	}

	/**
	 * Pointer to last node of the circular linked list
	 */
	private Node rear;
	
	/**
	 * Number of nodes in the CLL
	 */
	private int size;
	
	/**
	 * Initializes this list to empty
	 */
    public PartialTreeList() {
    	rear = null;
    	size = 0;
    }

    /**
     * Adds a new tree to the end of the list
     * 
     * @param tree Tree to be added to the end of the list
     */
    public void append(PartialTree tree) {
    	Node ptr = new Node(tree);
    	if (rear == null) {
    		ptr.next = ptr;
    	} else {
    		ptr.next = rear.next;
    		rear.next = ptr;
    	}
    	rear = ptr;
    	size++;
    }

    /**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) 
	{
		// Create empty list of partial trees
		PartialTreeList L = new PartialTreeList();
		// For every vertex v of the graph
		for (Vertex v : graph.vertices)
		{
			// Make a new partial tree containing only the vertex v
			PartialTree T = new PartialTree(v);
			// For every neighbor of the vertex v
			for (Vertex.Neighbor ptr = v.neighbors; ptr != null; ptr = ptr.next)
			{
				// Create an edge containing v and the neighbor
				Arc edge = new Arc(v, ptr.vertex, ptr.weight);
				// Insert the edge into the heap associated with T
				T.getArcs().insert(edge);
			}
			L.append(T);
		}
		return L;
	}
	
	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * for that graph
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<Arc> execute(PartialTreeList ptlist) 
	{
		ArrayList<Arc> list = new ArrayList<Arc>();
		while (ptlist.size() > 1)
		{
			PartialTree PTX = ptlist.remove();
			Arc alpha;
			Vertex v1;
			Vertex v2;
			do
			{
				alpha = PTX.getArcs().deleteMin();
				v1 = alpha.getv1();
				v2 = alpha.getv2();
			} while (v2.getRoot() == v1.getRoot());
			System.out.println(alpha.toString() + " is a component of the minimum spanning tree.");
			PartialTree PTY = ptlist.removeTreeContaining(v2);
			PTX.merge(PTY);
			ptlist.append(PTX);
			list.add(alpha);
		}
		return list;
	}
	
    /**
     * Removes the tree that is at the front of the list.
     * 
     * @return The tree that is removed from the front
     * @throws NoSuchElementException If the list is empty
     */
    public PartialTree remove() 
    throws NoSuchElementException {
    			
    	if (rear == null) {
    		throw new NoSuchElementException("list is empty");
    	}
    	PartialTree ret = rear.next.tree;
    	if (rear.next == rear) {
    		rear = null;
    	} else {
    		rear.next = rear.next.next;
    	}
    	size--;
    	return ret;
    		
    }

    /**
     * Removes the tree in this list that contains a given vertex.
     * 
     * @param vertex Vertex whose tree is to be removed
     * @return The tree that is removed
     * @throws NoSuchElementException If there is no matching tree
     */
    public PartialTree removeTreeContaining(Vertex vertex) 
    throws NoSuchElementException 
    {
    		if (size == 0)
    		{
    			throw new NoSuchElementException("No matching tree.");
    		}
    		Node ptr = rear.next;
    		Node prev = rear;
    		do
    		{
    			if (ptr.tree.getRoot() == vertex.getRoot())
    			{
    				if (ptr == rear)
    				{
    					if (rear == rear.next)
    					{
    						size = 0;
    						rear = null;
    						return ptr.tree;
    					}
    					else
    					{
    						size--;
    						rear = prev;
    						rear.next = ptr.next;
    						return ptr.tree;
    					}
    				}
    				else
    				{
    					prev.next = ptr.next;
    					size--;
    					return ptr.tree;
    				}
    			}
    			prev = ptr;
    			ptr = ptr.next;
    		} while (ptr != rear.next);
    		
    		throw new NoSuchElementException("No matching tree.");
     }
    
    /**
     * Gives the number of trees in this list
     * 
     * @return Number of trees
     */
    public int size() {
    	return size;
    }
    
    /**
     * Returns an Iterator that can be used to step through the trees in this list.
     * The iterator does NOT support remove.
     * 
     * @return Iterator for this list
     */
    public Iterator<PartialTree> iterator() {
    	return new PartialTreeListIterator(this);
    }
    
    private class PartialTreeListIterator implements Iterator<PartialTree> {
    	
    	private PartialTreeList.Node ptr;
    	private int rest;
    	
    	public PartialTreeListIterator(PartialTreeList target) {
    		rest = target.size;
    		ptr = rest > 0 ? target.rear.next : null;
    	}
    	
    	public PartialTree next() 
    	throws NoSuchElementException {
    		if (rest <= 0) {
    			throw new NoSuchElementException();
    		}
    		PartialTree ret = ptr.tree;
    		ptr = ptr.next;
    		rest--;
    		return ret;
    	}
    	
    	public boolean hasNext() {
    		return rest != 0;
    	}
    	
    	public void remove() 
    	throws UnsupportedOperationException {
    		throw new UnsupportedOperationException();
    	}
    	
    }
}


