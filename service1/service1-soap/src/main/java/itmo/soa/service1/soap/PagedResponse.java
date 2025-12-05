package itmo.soa.service1.soap;

import itmo.soa.service1.soap.dto.HumanBeingDTO;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * Обертка для пагинированного ответа в SOAP
 */
@XmlRootElement(name = "PagedResponse", namespace = "http://soap.service1.soa.itmo/")
@XmlAccessorType(XmlAccessType.FIELD)
public class PagedResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(namespace = "http://soap.service1.soa.itmo/")
    private List<HumanBeingDTO> content;

    @XmlElement(namespace = "http://soap.service1.soa.itmo/")
    private long totalElements;

    @XmlElement(namespace = "http://soap.service1.soa.itmo/")
    private long totalPages;

    @XmlElement(namespace = "http://soap.service1.soa.itmo/")
    private int size;

    @XmlElement(namespace = "http://soap.service1.soa.itmo/")
    private int number;

    @XmlElement(namespace = "http://soap.service1.soa.itmo/")
    private boolean first;

    @XmlElement(namespace = "http://soap.service1.soa.itmo/")
    private boolean last;

    public PagedResponse() {}

    public PagedResponse(List<HumanBeingDTO> content, long totalElements, long totalPages,
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
    public List<HumanBeingDTO> getContent() { return content; }
    public void setContent(List<HumanBeingDTO> content) { this.content = content; }

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
