
package itmo.soa.adapter.soap.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for pagedResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="pagedResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="content" type="{http://soap.service1.soa.itmo/}humanBeingDTO" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="totalElements" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="totalPages" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="size" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="number" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="first" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="last" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pagedResponse", propOrder = {
    "content",
    "totalElements",
    "totalPages",
    "size",
    "number",
    "first",
    "last"
})
public class PagedResponse {

    protected List<HumanBeingDTO> content;
    protected long totalElements;
    protected long totalPages;
    protected int size;
    protected int number;
    protected boolean first;
    protected boolean last;

    /**
     * Gets the value of the content property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HumanBeingDTO }
     * 
     * 
     */
    public List<HumanBeingDTO> getContent() {
        if (content == null) {
            content = new ArrayList<HumanBeingDTO>();
        }
        return this.content;
    }

    /**
     * Gets the value of the totalElements property.
     * 
     */
    public long getTotalElements() {
        return totalElements;
    }

    /**
     * Sets the value of the totalElements property.
     * 
     */
    public void setTotalElements(long value) {
        this.totalElements = value;
    }

    /**
     * Gets the value of the totalPages property.
     * 
     */
    public long getTotalPages() {
        return totalPages;
    }

    /**
     * Sets the value of the totalPages property.
     * 
     */
    public void setTotalPages(long value) {
        this.totalPages = value;
    }

    /**
     * Gets the value of the size property.
     * 
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     */
    public void setSize(int value) {
        this.size = value;
    }

    /**
     * Gets the value of the number property.
     * 
     */
    public int getNumber() {
        return number;
    }

    /**
     * Sets the value of the number property.
     * 
     */
    public void setNumber(int value) {
        this.number = value;
    }

    /**
     * Gets the value of the first property.
     * 
     */
    public boolean isFirst() {
        return first;
    }

    /**
     * Sets the value of the first property.
     * 
     */
    public void setFirst(boolean value) {
        this.first = value;
    }

    /**
     * Gets the value of the last property.
     * 
     */
    public boolean isLast() {
        return last;
    }

    /**
     * Sets the value of the last property.
     * 
     */
    public void setLast(boolean value) {
        this.last = value;
    }

}
