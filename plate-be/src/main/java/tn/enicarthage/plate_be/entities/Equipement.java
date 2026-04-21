package tn.enicarthage.plate_be.entities;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "equipements")
public class Equipement {

    @Id
    @Column(name = "id_equipement", length = 50)
    private String idEquipement;

    @Column(name = "nom_equipement", nullable = false, length = 100)
    private String nomEquipement;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "modele", length = 100)
    private String modele;

    @Column(name = "num_serie", length = 100)
    private String numSerie;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", nullable = false, length = 30)
    private ETAT_EQUIPEMENT etat;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_achat")
    private Date dateAchat;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_fin_garantie")
    private Date dateFinGarantie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salle_id")
    private Salle salle;

    @OneToMany(mappedBy = "equipement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoriqueMaintenance> historiquesMaintenance;

    @OneToMany(mappedBy = "equipement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MaintenancePreventive> maintenancesPreventives;

    @OneToMany(mappedBy = "equipement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets;


    public Equipement() {
    }

    public Equipement(String idEquipement,
                      String nomEquipement,
                      String type,
                      String modele,
                      String numSerie,
                      ETAT_EQUIPEMENT etat,
                      Date dateAchat,
                      Date dateFinGarantie) {
        this.idEquipement = idEquipement;
        this.nomEquipement = nomEquipement;
        this.type = type;
        this.modele = modele;
        this.numSerie = numSerie;
        this.etat = etat;
        this.dateAchat = dateAchat;
        this.dateFinGarantie = dateFinGarantie;
    }


    public String getIdEquipement() {
        return idEquipement;
    }

    public void setIdEquipement(String idEquipement) {
        this.idEquipement = idEquipement;
    }

    public String getNomEquipement() {
        return nomEquipement;
    }

    public void setNomEquipement(String nomEquipement) {
        this.nomEquipement = nomEquipement;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public String getNumSerie() {
        return numSerie;
    }

    public void setNumSerie(String numSerie) {
        this.numSerie = numSerie;
    }

    public ETAT_EQUIPEMENT getEtat() {
        return etat;
    }

    public void setEtat(ETAT_EQUIPEMENT etat) {
        this.etat = etat;
    }

    public Date getDateAchat() {
        return dateAchat;
    }

    public void setDateAchat(Date dateAchat) {
        this.dateAchat = dateAchat;
    }

    public Date getDateFinGarantie() {
        return dateFinGarantie;
    }

    public void setDateFinGarantie(Date dateFinGarantie) {
        this.dateFinGarantie = dateFinGarantie;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    public List<HistoriqueMaintenance> getHistoriquesMaintenance() {
        return historiquesMaintenance;
    }

    public void setHistoriquesMaintenance(List<HistoriqueMaintenance> historiquesMaintenance) {
        this.historiquesMaintenance = historiquesMaintenance;
    }

    public List<MaintenancePreventive> getMaintenancesPreventives() {
        return maintenancesPreventives;
    }

    public void setMaintenancesPreventives(List<MaintenancePreventive> maintenancesPreventives) {
        this.maintenancesPreventives = maintenancesPreventives;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @Override
    public String toString() {
        return "Equipement{" +
                "idEquipement='" + idEquipement + '\'' +
                ", nomEquipement='" + nomEquipement + '\'' +
                ", type='" + type + '\'' +
                ", modele='" + modele + '\'' +
                ", numSerie='" + numSerie + '\'' +
                ", etat=" + etat +
                ", dateAchat=" + dateAchat +
                ", dateFinGarantie=" + dateFinGarantie +
                '}';
    }
}
