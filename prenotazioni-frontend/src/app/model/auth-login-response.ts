export interface AuthLoginResponse {
  token: string;
  tokenType: string;
  idAccount: number;
  idUtente: number | null;
  idCollaboratore: number | null;
  email: string;
  ruolo: string;
}
