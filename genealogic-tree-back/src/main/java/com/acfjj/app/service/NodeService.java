package com.acfjj.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.acfjj.app.model.Node;
import com.acfjj.app.model.PersonInfo;
import com.acfjj.app.model.Tree;
import com.acfjj.app.model.TreeNodes;
import com.acfjj.app.repository.PersonInfoRepository;
import com.acfjj.app.repository.TreeNodesRepository;
import com.acfjj.app.repository.TreeRepository;
import com.acfjj.app.repository.UserRepository;
import com.acfjj.app.repository.NodeRepository;
import com.acfjj.app.utils.Misc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class NodeService {

    @Autowired
    NodeRepository nodeRepository;
    @Autowired
    PersonInfoRepository personInfoRepository;
    @Autowired
    TreeRepository treeRepository;
    @Autowired
    TreeNodesRepository treeNodesRepository;
    @Autowired
    UserRepository userRepository;

    public List<Node> getAllNodes() {
        List<Node> nodes = new ArrayList<>();
        nodeRepository.findAll().forEach(nodes::add);
        return nodes;
    }

    public Node getNode(Long id) {
        return nodeRepository.findById(id).orElse(null);
    }

    public void addNode(Node node) {
    	personInfoRepository.save(node.getPersonInfo());
        nodeRepository.save(node);
        for (TreeNodes treeNode : node.getTreeNodes()) {
    		if(treeNode != null)
            treeNodesRepository.save(treeNode);
        }
        return;
    }
    
    public void addParent(Node node, Node parentNode) {
    	 if (node != null && parentNode != null) {
    	        if (node.getParent1() == null) {
    	            node.setParent1(parentNode);
    	        } else if (node.getParent2() == null) {
    	            node.setParent2(parentNode);
    	        }
    	        updateNode(node.getId(), node);
    	    }
    	 return;
    }
    
    public void updateParent(Long nodeId, Long parentNodeId, int whichParent) {
    	if(nodeId == parentNodeId) 
    		return;
        Node node = getNode(nodeId);
        Node newParent = getNode(parentNodeId);
        if (node != null && whichParent == 1) {
            node.setParent1(newParent);
            updateNode(nodeId, node);
        } else if (node != null && whichParent == 2) {
            node.setParent2(newParent);
            updateNode(nodeId, node);
        }
        return;
    }


   public void deleteNode(Long id) {
        Node node = getNode(id);
        if (node != null) {        	
            if (node.isOrphan()) {
                PersonInfo personInfo = node.getPersonInfo();
                if (personInfo != null) {
                	nodeRepository.deleteById(id);
                    personInfoRepository.deleteById(personInfo.getId());
                    return;
                }
            }
            nodeRepository.deleteById(id);
        }
        return;
    }
   
   public void removeLinks(Long id) {
       Node node = getNode(id);
       if (node != null) {        	
           if (node.getParent1() != null) {
        	   node.setParent1(null);
        	   updateNode(node.getId(),node);
           }
           if (node.getParent2() != null) {
        	   node.setParent2(null);
        	   updateNode(node.getId(),node);        	   
           }
           if (node.getPartner() != null) {
        	   Node partner = node.getPartner();
        	   node.setPartner(null);
        	   updateNode(node.getId(),node);  
        	   partner.setPartner(null);
        	   updateNode(partner.getId(),partner); 
           }
           if (node.getSiblings() != null) {
        	   for(Node siblings : node.getSiblings()) {
        		   siblings.removeSiblings(node);
        		   updateNode(siblings.getId(),siblings); 
        	   }
        	   node.setSiblings(null);
        	   updateNode(node.getId(),node);  
           }
           if (node.getExPartners() != null) {
        	   for(Node exPartners : node.getExPartners()) {
        		   exPartners.removeExPartners(node);
        		   updateNode(exPartners.getId(),exPartners); 
        	   }
        	   node.setExPartners(null);
        	   updateNode(node.getId(),node);  
           }
       }

   }

    public void updateNode(Long id, Node node) {
        if (getNode(id) != null && node.getId() == id) {
            nodeRepository.save(node);
            personInfoRepository.save(node.getPersonInfo());
        }
    }

    public List<Node> getParentsOfNode(Long id) {
        Node node = getNode(id);
        List<Node> parents = new ArrayList<>();
        parents.add(node.getParent1());
        parents.add(node.getParent2());
        return parents;
    }
    
    
    public boolean doesNodeBelongToTree(Node node, Long treeId) {
        Tree tree = treeRepository.findById(treeId).orElse(null);
        if (node == null || tree == null) {
            return false;
        }

        Set<TreeNodes> treeNodes = node.getTreeNodes();
        for (TreeNodes treeNode : treeNodes) {
            if (treeNode.getTree().equals(tree)) {
                return true;
            }
        }
        return false;
    }
    
    public Integer getGenerationGap(Node node1, Node node2) {
    	List<Tree> trees1 = node1.getTrees();
    	List<Tree> trees2 = node2.getTrees();
    	trees1.retainAll(trees2);
    	List<Integer> diffList = new ArrayList<>();
    	for(Tree tree : trees1) {
    		diffList.add(tree.getTreeNodesByNode(node1).getDepth() - tree.getTreeNodesByNode(node2).getDepth());
    	}
    	Integer diff = Misc.findMaxFrequency(diffList);

    	//tous tree avec 2 nodes
    	//différence prof des 2 
    	//celle qui apparait plus souvent comme prof
    	return null;
    }
    
    public Node getNodeByNameAndBirthInfo(String lastName, String firstName, LocalDate dateOfBirth, String countryOfBirth, String cityofBirth) {
		Node nodeFound = null;
		PersonInfo personInfoFound = personInfoRepository.findByLastNameAndFirstNameAndDateOfBirthAndCountryOfBirthAndCityOfBirth(lastName, firstName, dateOfBirth, countryOfBirth, cityofBirth); 
		if(!Objects.isNull(personInfoFound)) {
			nodeFound = nodeRepository.findByPersonInfo(personInfoFound);
		}
		return nodeFound;
	}
}
