
package itmo.soa.adapter.soap.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for humanBeingDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="humanBeingDTO"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="coordinates" type="{http://soap.service1.soa.itmo/}Coordinates" minOccurs="0"/&gt;
 *         &lt;element name="creationDate" type="{http://soap.service1.soa.itmo/}localDateTime" minOccurs="0"/&gt;
 *         &lt;element name="realHero" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="hasToothpick" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="impactSpeed" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="weaponType" type="{http://soap.service1.soa.itmo/}weaponType" minOccurs="0"/&gt;
 *         &lt;element name="mood" type="{http://soap.service1.soa.itmo/}mood" minOccurs="0"/&gt;
 *         &lt;element name="car" type="{http://soap.service1.soa.itmo/}Car" minOccurs="0"/&gt;
 *         &lt;element name="teamId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "humanBeingDTO", propOrder = {
    "id",
    "name",
    "coordinates",
    "creationDate",
    "realHero",
    "hasToothpick",
    "impactSpeed",
    "weaponType",
    "mood",
    "car",
    "teamId"
})
public class HumanBeingDTO {

    protected Long id;
    protected String name;
    protected Coordinates coordinates;
    protected LocalDateTime creationDate;
    protected Boolean realHero;
    protected Boolean hasToothpick;
    protected Integer impactSpeed;
    @XmlSchemaType(name = "string")
    protected WeaponType weaponType;
    @XmlSchemaType(name = "string")
    protected Mood mood;
    protected Car car;
    protected Integer teamId;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setId(Long value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the coordinates property.
     * 
     * @return
     *     possible object is
     *     {@link Coordinates }
     *     
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Sets the value of the coordinates property.
     * 
     * @param value
     *     allowed object is
     *     {@link Coordinates }
     *     
     */
    public void setCoordinates(Coordinates value) {
        this.coordinates = value;
    }

    /**
     * Gets the value of the creationDate property.
     * 
     * @return
     *     possible object is
     *     {@link LocalDateTime }
     *     
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the value of the creationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocalDateTime }
     *     
     */
    public void setCreationDate(LocalDateTime value) {
        this.creationDate = value;
    }

    /**
     * Gets the value of the realHero property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRealHero() {
        return realHero;
    }

    /**
     * Sets the value of the realHero property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRealHero(Boolean value) {
        this.realHero = value;
    }

    /**
     * Gets the value of the hasToothpick property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHasToothpick() {
        return hasToothpick;
    }

    /**
     * Sets the value of the hasToothpick property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHasToothpick(Boolean value) {
        this.hasToothpick = value;
    }

    /**
     * Gets the value of the impactSpeed property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getImpactSpeed() {
        return impactSpeed;
    }

    /**
     * Sets the value of the impactSpeed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setImpactSpeed(Integer value) {
        this.impactSpeed = value;
    }

    /**
     * Gets the value of the weaponType property.
     * 
     * @return
     *     possible object is
     *     {@link WeaponType }
     *     
     */
    public WeaponType getWeaponType() {
        return weaponType;
    }

    /**
     * Sets the value of the weaponType property.
     * 
     * @param value
     *     allowed object is
     *     {@link WeaponType }
     *     
     */
    public void setWeaponType(WeaponType value) {
        this.weaponType = value;
    }

    /**
     * Gets the value of the mood property.
     * 
     * @return
     *     possible object is
     *     {@link Mood }
     *     
     */
    public Mood getMood() {
        return mood;
    }

    /**
     * Sets the value of the mood property.
     * 
     * @param value
     *     allowed object is
     *     {@link Mood }
     *     
     */
    public void setMood(Mood value) {
        this.mood = value;
    }

    /**
     * Gets the value of the car property.
     * 
     * @return
     *     possible object is
     *     {@link Car }
     *     
     */
    public Car getCar() {
        return car;
    }

    /**
     * Sets the value of the car property.
     * 
     * @param value
     *     allowed object is
     *     {@link Car }
     *     
     */
    public void setCar(Car value) {
        this.car = value;
    }

    /**
     * Gets the value of the teamId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTeamId() {
        return teamId;
    }

    /**
     * Sets the value of the teamId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTeamId(Integer value) {
        this.teamId = value;
    }

}
