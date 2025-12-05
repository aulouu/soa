
package itmo.soa.adapter.soap.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the itmo.soa.adapter.soap.generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _HumanBeing_QNAME = new QName("http://soap.service1.soa.itmo/", "HumanBeing");
    private final static QName _PagedResponse_QNAME = new QName("http://soap.service1.soa.itmo/", "PagedResponse");
    private final static QName _CountByMood_QNAME = new QName("http://soap.service1.soa.itmo/", "countByMood");
    private final static QName _CountByMoodResponse_QNAME = new QName("http://soap.service1.soa.itmo/", "countByMoodResponse");
    private final static QName _CountByNameStartsWith_QNAME = new QName("http://soap.service1.soa.itmo/", "countByNameStartsWith");
    private final static QName _CountByNameStartsWithResponse_QNAME = new QName("http://soap.service1.soa.itmo/", "countByNameStartsWithResponse");
    private final static QName _CreateHumanBeing_QNAME = new QName("http://soap.service1.soa.itmo/", "createHumanBeing");
    private final static QName _CreateHumanBeingResponse_QNAME = new QName("http://soap.service1.soa.itmo/", "createHumanBeingResponse");
    private final static QName _DeleteHumanBeing_QNAME = new QName("http://soap.service1.soa.itmo/", "deleteHumanBeing");
    private final static QName _DeleteHumanBeingResponse_QNAME = new QName("http://soap.service1.soa.itmo/", "deleteHumanBeingResponse");
    private final static QName _GetAllHumanBeings_QNAME = new QName("http://soap.service1.soa.itmo/", "getAllHumanBeings");
    private final static QName _GetAllHumanBeingsResponse_QNAME = new QName("http://soap.service1.soa.itmo/", "getAllHumanBeingsResponse");
    private final static QName _GetHumanBeingById_QNAME = new QName("http://soap.service1.soa.itmo/", "getHumanBeingById");
    private final static QName _GetHumanBeingByIdResponse_QNAME = new QName("http://soap.service1.soa.itmo/", "getHumanBeingByIdResponse");
    private final static QName _GetUniqueImpactSpeeds_QNAME = new QName("http://soap.service1.soa.itmo/", "getUniqueImpactSpeeds");
    private final static QName _GetUniqueImpactSpeedsResponse_QNAME = new QName("http://soap.service1.soa.itmo/", "getUniqueImpactSpeedsResponse");
    private final static QName _UpdateHumanBeing_QNAME = new QName("http://soap.service1.soa.itmo/", "updateHumanBeing");
    private final static QName _UpdateHumanBeingResponse_QNAME = new QName("http://soap.service1.soa.itmo/", "updateHumanBeingResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: itmo.soa.adapter.soap.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link HumanBeingDTO }
     * 
     */
    public HumanBeingDTO createHumanBeingDTO() {
        return new HumanBeingDTO();
    }

    /**
     * Create an instance of {@link PagedResponse }
     * 
     */
    public PagedResponse createPagedResponse() {
        return new PagedResponse();
    }

    /**
     * Create an instance of {@link CountByMood }
     * 
     */
    public CountByMood createCountByMood() {
        return new CountByMood();
    }

    /**
     * Create an instance of {@link CountByMoodResponse }
     * 
     */
    public CountByMoodResponse createCountByMoodResponse() {
        return new CountByMoodResponse();
    }

    /**
     * Create an instance of {@link CountByNameStartsWith }
     * 
     */
    public CountByNameStartsWith createCountByNameStartsWith() {
        return new CountByNameStartsWith();
    }

    /**
     * Create an instance of {@link CountByNameStartsWithResponse }
     * 
     */
    public CountByNameStartsWithResponse createCountByNameStartsWithResponse() {
        return new CountByNameStartsWithResponse();
    }

    /**
     * Create an instance of {@link CreateHumanBeing }
     * 
     */
    public CreateHumanBeing createCreateHumanBeing() {
        return new CreateHumanBeing();
    }

    /**
     * Create an instance of {@link CreateHumanBeingResponse }
     * 
     */
    public CreateHumanBeingResponse createCreateHumanBeingResponse() {
        return new CreateHumanBeingResponse();
    }

    /**
     * Create an instance of {@link DeleteHumanBeing }
     * 
     */
    public DeleteHumanBeing createDeleteHumanBeing() {
        return new DeleteHumanBeing();
    }

    /**
     * Create an instance of {@link DeleteHumanBeingResponse }
     * 
     */
    public DeleteHumanBeingResponse createDeleteHumanBeingResponse() {
        return new DeleteHumanBeingResponse();
    }

    /**
     * Create an instance of {@link GetAllHumanBeings }
     * 
     */
    public GetAllHumanBeings createGetAllHumanBeings() {
        return new GetAllHumanBeings();
    }

    /**
     * Create an instance of {@link GetAllHumanBeingsResponse }
     * 
     */
    public GetAllHumanBeingsResponse createGetAllHumanBeingsResponse() {
        return new GetAllHumanBeingsResponse();
    }

    /**
     * Create an instance of {@link GetHumanBeingById }
     * 
     */
    public GetHumanBeingById createGetHumanBeingById() {
        return new GetHumanBeingById();
    }

    /**
     * Create an instance of {@link GetHumanBeingByIdResponse }
     * 
     */
    public GetHumanBeingByIdResponse createGetHumanBeingByIdResponse() {
        return new GetHumanBeingByIdResponse();
    }

    /**
     * Create an instance of {@link GetUniqueImpactSpeeds }
     * 
     */
    public GetUniqueImpactSpeeds createGetUniqueImpactSpeeds() {
        return new GetUniqueImpactSpeeds();
    }

    /**
     * Create an instance of {@link GetUniqueImpactSpeedsResponse }
     * 
     */
    public GetUniqueImpactSpeedsResponse createGetUniqueImpactSpeedsResponse() {
        return new GetUniqueImpactSpeedsResponse();
    }

    /**
     * Create an instance of {@link UpdateHumanBeing }
     * 
     */
    public UpdateHumanBeing createUpdateHumanBeing() {
        return new UpdateHumanBeing();
    }

    /**
     * Create an instance of {@link UpdateHumanBeingResponse }
     * 
     */
    public UpdateHumanBeingResponse createUpdateHumanBeingResponse() {
        return new UpdateHumanBeingResponse();
    }

    /**
     * Create an instance of {@link Coordinates }
     * 
     */
    public Coordinates createCoordinates() {
        return new Coordinates();
    }

    /**
     * Create an instance of {@link LocalDateTime }
     * 
     */
    public LocalDateTime createLocalDateTime() {
        return new LocalDateTime();
    }

    /**
     * Create an instance of {@link Car }
     * 
     */
    public Car createCar() {
        return new Car();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HumanBeingDTO }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link HumanBeingDTO }{@code >}
     */
    @XmlElementDecl(namespace = "http://soap.service1.soa.itmo/", name = "HumanBeing")
    public JAXBElement<HumanBeingDTO> createHumanBeing(HumanBeingDTO value) {
        return new JAXBElement<HumanBeingDTO>(_HumanBeing_QNAME, HumanBeingDTO.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PagedResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link PagedResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://soap.service1.soa.itmo/", name = "PagedResponse")
    public JAXBElement<PagedResponse> createPagedResponse(PagedResponse value) {
        return new JAXBElement<PagedResponse>(_PagedResponse_QNAME, PagedResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountByMood }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CountByMood }{@code >}
     */
    @XmlElementDecl(namespace = "http://soap.service1.soa.itmo/", name = "countByMood")
    public JAXBElement<CountByMood> createCountByMood(CountByMood value) {
        return new JAXBElement<CountByMood>(_CountByMood_QNAME, CountByMood.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountByMoodResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CountByMoodResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://soap.service1.soa.itmo/", name = "countByMoodResponse")
    public JAXBElement<CountByMoodResponse> createCountByMoodResponse(CountByMoodResponse value) {
        return new JAXBElement<CountByMoodResponse>(_CountByMoodResponse_QNAME, CountByMoodResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountByNameStartsWith }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CountByNameStartsWith }{@code >}
     */
    @XmlElementDecl(namespace = "http://soap.service1.soa.itmo/", name = "countByNameStartsWith")
    public JAXBElement<CountByNameStartsWith> createCountByNameStartsWith(CountByNameStartsWith value) {
        return new JAXBElement<CountByNameStartsWith>(_CountByNameStartsWith_QNAME, CountByNameStartsWith.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountByNameStartsWithResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CountByNameStartsWithResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://soap.service1.soa.itmo/", name = "countByNameStartsWithResponse")
    public JAXBElement<CountByNameStartsWithResponse> createCountByNameStartsWithResponse(CountByNameStartsWithResponse value) {
        return new JAXBElement<CountByNameStartsWithResponse>(_CountByNameStartsWithResponse_QNAME, CountByNameStartsWithResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateHumanBeing }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CreateHumanBeing }{@code >}
     */
    @XmlElementDecl(namespace = "http://soap.service1.soa.itmo/", name = "createHumanBeing")
    public JAXBElement<CreateHumanBeing> createCreateHumanBeing(CreateHumanBeing value) {
        return new JAXBElement<CreateHumanBeing>(_CreateHumanBeing_QNAME, CreateHumanBeing.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateHumanBeingResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CreateHumanBeingResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://soap.service1.soa.itmo/", name = "createHumanBeingResponse")
    public JAXBElement<CreateHumanBeingResponse> createCreateHumanBeingResponse(CreateHumanBeingResponse value) {
        return new JAXBElement<CreateHumanBeingResponse>(_CreateHumanBeingResponse_QNAME, CreateHumanBeingResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteHumanBeing }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DeleteHumanBeing }{@code >}
     */
    @XmlElementDecl(namespace = "http://soap.service1.soa.itmo/", name = "deleteHumanBeing")
    public JAXBElement<DeleteHumanBeing> createDeleteHumanBeing(DeleteHumanBeing value) {
        return new JAXBElement<DeleteHumanBeing>(_DeleteHumanBeing_QNAME, DeleteHumanBeing.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteHumanBeingResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DeleteHumanBeingResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://soap.service1.soa.itmo/", name = "deleteHumanBeingResponse")
    public JAXBElement<DeleteHumanBeingResponse> createDeleteHumanBeingResponse(DeleteHumanBeingResponse value) {
        return new JAXBElement<DeleteHumanBeingResponse>(_DeleteHumanBeingResponse_QNAME, DeleteHumanBeingResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllHumanBeings }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetAllHumanBeings }{@code >}
     */
    @XmlElementDecl(namespace = "http://soap.service1.soa.itmo/", name = "getAllHumanBeings")
    public JAXBElement<GetAllHumanBeings> createGetAllHumanBeings(GetAllHumanBeings value) {
        return new JAXBElement<GetAllHumanBeings>(_GetAllHumanBeings_QNAME, GetAllHumanBeings.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllHumanBeingsResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetAllHumanBeingsResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://soap.service1.soa.itmo/", name = "getAllHumanBeingsResponse")
    public JAXBElement<GetAllHumanBeingsResponse> createGetAllHumanBeingsResponse(GetAllHumanBeingsResponse value) {
        return new JAXBElement<GetAllHumanBeingsResponse>(_GetAllHumanBeingsResponse_QNAME, GetAllHumanBeingsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetHumanBeingById }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetHumanBeingById }{@code >}
     */
    @XmlElementDecl(namespace = "http://soap.service1.soa.itmo/", name = "getHumanBeingById")
    public JAXBElement<GetHumanBeingById> createGetHumanBeingById(GetHumanBeingById value) {
        return new JAXBElement<GetHumanBeingById>(_GetHumanBeingById_QNAME, GetHumanBeingById.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetHumanBeingByIdResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetHumanBeingByIdResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://soap.service1.soa.itmo/", name = "getHumanBeingByIdResponse")
    public JAXBElement<GetHumanBeingByIdResponse> createGetHumanBeingByIdResponse(GetHumanBeingByIdResponse value) {
        return new JAXBElement<GetHumanBeingByIdResponse>(_GetHumanBeingByIdResponse_QNAME, GetHumanBeingByIdResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUniqueImpactSpeeds }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetUniqueImpactSpeeds }{@code >}
     */
    @XmlElementDecl(namespace = "http://soap.service1.soa.itmo/", name = "getUniqueImpactSpeeds")
    public JAXBElement<GetUniqueImpactSpeeds> createGetUniqueImpactSpeeds(GetUniqueImpactSpeeds value) {
        return new JAXBElement<GetUniqueImpactSpeeds>(_GetUniqueImpactSpeeds_QNAME, GetUniqueImpactSpeeds.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUniqueImpactSpeedsResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetUniqueImpactSpeedsResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://soap.service1.soa.itmo/", name = "getUniqueImpactSpeedsResponse")
    public JAXBElement<GetUniqueImpactSpeedsResponse> createGetUniqueImpactSpeedsResponse(GetUniqueImpactSpeedsResponse value) {
        return new JAXBElement<GetUniqueImpactSpeedsResponse>(_GetUniqueImpactSpeedsResponse_QNAME, GetUniqueImpactSpeedsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateHumanBeing }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link UpdateHumanBeing }{@code >}
     */
    @XmlElementDecl(namespace = "http://soap.service1.soa.itmo/", name = "updateHumanBeing")
    public JAXBElement<UpdateHumanBeing> createUpdateHumanBeing(UpdateHumanBeing value) {
        return new JAXBElement<UpdateHumanBeing>(_UpdateHumanBeing_QNAME, UpdateHumanBeing.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateHumanBeingResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link UpdateHumanBeingResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://soap.service1.soa.itmo/", name = "updateHumanBeingResponse")
    public JAXBElement<UpdateHumanBeingResponse> createUpdateHumanBeingResponse(UpdateHumanBeingResponse value) {
        return new JAXBElement<UpdateHumanBeingResponse>(_UpdateHumanBeingResponse_QNAME, UpdateHumanBeingResponse.class, null, value);
    }

}
