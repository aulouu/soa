package itmo.soa.adapter.model;

import java.util.List;

public class PagedResponseDTO {
    private List<HumanBeingDTO> content;
    private long totalElements;
    private long totalPages;
    private int size;
    private int number;
    private boolean first;
    private boolean last;

    public PagedResponseDTO() {}

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
