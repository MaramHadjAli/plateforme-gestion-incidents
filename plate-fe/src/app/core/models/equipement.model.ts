export interface Equipement {
  idEquipement: string;
  nomEquipement: string;
  type: string;
  modele: string;
  numSerie: string;
  etat: 'FONCTIONNELLE' | 'EN_PANNE' | 'EN_MAINTENNANCE';
  idSalle: string;
}