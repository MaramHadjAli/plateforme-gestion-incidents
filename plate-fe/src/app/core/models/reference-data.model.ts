export interface SalleSummary {
  idSalle: string;
  nomSalle: string;
  etage: string;
  batiment: string;
}

export interface EquipementSummary {
  idEquipement: string;
  nomEquipement: string;
  type: string;
  modele: string;
  numSerie: string;
  etat: string | null;
  idSalle: string | null;
}

