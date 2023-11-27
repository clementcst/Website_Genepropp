package com.acfjj.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;


@SuppressWarnings("serial")
@Entity
public class Tree implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
	private int privacy;
	
	private long viewOfMonth;
	private long viewOfYear;
	
	@JsonIgnore
	@OneToMany(mappedBy = "tree",fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<TreeNodes> treeNodes = new HashSet<>();
	
	
	public Tree() {
		super();
	}
	public Tree(String name, int privacy,  TreeNodes treeNode) {
		this();
		this.name=name;
		this.privacy = privacy;
		this.treeNodes.add(treeNode);
		this.viewOfMonth = 0;
		this.viewOfYear = 0;
	}	
	
	//utiliser ce constructeur à la création
	public Tree(String name, int privacy) {
		this(name, privacy, null);
	}
	
	/*Getters & Setters*/
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrivacy() {
		return privacy;
	}

	public void setPrivacy(int privacy) {
		this.privacy = privacy;
	}
	
	public Set<TreeNodes> getTreeNodes() {
		return treeNodes;
	}
	
	public TreeNodes getTreeNodesByNode(Node node) {
		for(TreeNodes treeNodes : getTreeNodes()) {
			if(treeNodes.getNode().equals(node)) {
				return treeNodes;
			}
		}		 
		return null;
	}
	
	public void addTreeNodes(TreeNodes treeNode) {
		this.getTreeNodes().add(treeNode);
	}

	public void setTreeNodes(Set<TreeNodes> treeNode) {
		this.treeNodes = treeNode;
	}

	//	public void removeTreeNodes(TreeNodes treeNode) {
	//		this.getNodes().remove(treeNode);
	//	}

	public boolean isTreePublic() {
		return this.getPrivacy() == 1;		
	}
	
	public long getViewOfMonth() {
		return viewOfMonth;
	}

	public void setViewOfMonth(long viewOfMonth) {
		this.viewOfMonth = viewOfMonth;
	}

	public long getViewOfYear() {
		return viewOfYear;
	}

	public void setViewOfYear(long viewOfYear) {
		this.viewOfYear = viewOfYear;
	}
	
	public List<Node> getNodes() {
		List<Node> nodes = new ArrayList<>();
		for (TreeNodes treeNodes : treeNodes) {
			nodes.add(treeNodes.getNode());
		}
		return nodes;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj) {
	        return true;
	    }
	    if (obj == null || getClass() != obj.getClass()) {
	        return false;
	    }
	    Tree otherTree = (Tree) obj;
	    return (id != null && otherTree.id != null) ? 
	        id.equals(otherTree.id) &&
	        name.equals(otherTree.name) &&
	        viewOfMonth == otherTree.viewOfMonth &&
	        viewOfYear == otherTree.viewOfYear &&
	        privacy == otherTree.privacy  :
		    super.equals(obj);
	}

	@Override
	public String toString() {
		return "Tree [id=" + id 
				+ ", name=" + name 
				+ ", privacy=" + privacy 
				+ ", viewOfMonth=" + viewOfMonth
				+ ", viewOfYear=" + viewOfYear 
				+ ", nodes=" + treeNodes + "]";
	}	
	
}
