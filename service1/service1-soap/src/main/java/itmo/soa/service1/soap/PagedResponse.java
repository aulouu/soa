package itmo.soa.service1.soap;

import itmo.soa.service1.model.HumanBeing;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * Обертка для пагинированного ответа в SOAP
 */
@XmlRootElement(name = "PagedResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PagedResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement
    private List<HumanBeing> content;
    
    @XmlElement
    private long totalElements;
    
    @XmlElement
    private long totalPages;
    
    @XmlElement
    private int size;
    
    @XmlElement
    private int number;
    
    @XmlElement
    private boolean first;
    
    @XmlElement
    private boolean last;

    public PagedResponse() {}

    public PagedResponse(List<HumanBeing> content, long totalElements, long totalPages, 
                        int size, int number, boolean first, boolean last) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.size = size;
        this.number = number;
        this.first = first;
        this.last = last;
    }

    // Getters and Setters
    public List<HumanBeing> getContent() { return content; }
    public void setContent(List<HumanBeing> content) { this.content = content; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public long getTotalPages() { return totalPages; }
    public void setTotalPages(long totalPages) { this.totalPages = totalPages; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public boolean isFirst() { return first; }
    public void setFirst(boolean first) { this.first = first; }

    public boolean isLast() { return last; }
    public void setLast(boolean last) { this.last = last; }
}
