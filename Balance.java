
package budget;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Balance {
        public String dateDebut;
        public String dateFin;
        public Connection conn;
        public Balance(){}
        public int getMois(String date){
                Control c = new Control();
                String[] rep = c.spliter(dateDebut,"/");
                return Integer.parseInt(rep[1]);
        }
        public double[] getSum(int compte,String dateDebut,String dateFin)throws Exception{
                UtilDB db =new UtilDB();
                this.conn = db.getConn();
                double[] rep = new double[2];
                String req = "select sum(debit),sum(credit)  from ecriture where compte = "+compte+" and dateecriture between to_date('"+dateDebut+"','dd/mm/yyyy') and to_date('"+dateFin+"','dd/mm/yyyy')";	
                Statement stmt = conn.createStatement();
                ResultSet res = stmt.executeQuery(req);
                while(res.next()){
			rep[0] = res.getDouble(1);
                        rep[1] = res.getDouble(2);
		}
                return rep;
        }
        public Object[][] getEcriture(String dateDebut,String dateFin)throws Exception{
                int nb = 0;
                int[] compte = this.getCompte();
                Object[][] obj = new Object[compte.length][5]; 
                double[] dc = new double[2];
                for(int i = 0;i<compte.length;i++){
                    dc = this.getSum(compte[i],dateDebut,dateFin);
                    obj[i][0] = compte[i];
                    obj[i][1] = dc[0];
                    obj[i][2] = dc[1];
                    if((double)obj[i][1]>(double)obj[i][2]){
                          obj[i][3] = (double)obj[i][1]-(double)obj[i][2];
                          obj[i][4] = 0.0;
                    }
                    if((double)obj[i][1]<(double)obj[i][2]){
                          obj[i][4] = (double)obj[i][2]-(double)obj[i][1];
                          obj[i][3] = 0.0;
                    }
                     if((double)obj[i][1]==(double)obj[i][2]){
                            obj[i][3] = 0.0;
                            obj[i][4] = 0.0;
                     }
                }
                return obj;
        }
        public double getLcEngage(int id,String date)throws Exception{
                UtilDB db =new UtilDB();
                this.conn = db.getConn();
                Control c = new Control();
                double rep = 0;
                String[] d = c.spliter(date,"/");
                String d1 = "01/"+d[1]+d[2];
                String d2 = "31/"+d[1]+d[2];
                String req = "select sum(montantlignecreditengage)  from lignecreditengage join engagement on lignecreditengage.idEngagement=Engagement.idEngagement where idlignecredit = "+id;//+"and dateEngagement between to_date('"+d1+"','dd/mm/yyyy') and to_date('"+d2+"','dd/mm/yyyy')";	
                Statement stmt = conn.createStatement();
                ResultSet res = stmt.executeQuery(req);
                while(res.next()){
			rep = res.getDouble(1);
		}
                return rep;
        }
        public double getLcRestant(int id,String date)throws Exception{
                UtilDB db =new UtilDB();
                this.conn = db.getConn();
                Control c = new Control();
                double rep = 0;
                String[] d = c.spliter(date,"/");
                String d1 = "01/"+d[1]+d[2];
                String d2 = "31/"+d[1]+d[2];
                String req = "select sum(montantrestant)  from paiementlignecredit join paiement on paiementlignecredit.idPaiment=paiement.idPaiment where idlignecredit = "+id+"and datePaielment between to_date('"+d1+"','dd/mm/yyyy') and to_date('"+d2+"','dd/mm/yyyy')";	
                Statement stmt = conn.createStatement();
                ResultSet res = stmt.executeQuery(req);
                while(res.next()){
			rep = res.getDouble(1);
		}
                return rep;
        }
        public double getLcPayer(int id,String date)throws Exception{
                UtilDB db =new UtilDB();
                this.conn = db.getConn();
                Control c = new Control();
                double rep = 0;
                String[] d = c.spliter(date,"/");
                String d1 = "01/"+d[1]+d[2];
                String d2 = "31/"+d[1]+d[2];
                String req = "select sum(montantPaiementLigneCredit)  from paiementlignecredit join paiement on paiementlignecredit.idPaiment=paiement.idPaiment where idlignecredit = "+id+"and datePaielment between to_date('"+d1+"','dd/mm/yyyy') and to_date('"+d2+"','dd/mm/yyyy')";	
                Statement stmt = conn.createStatement();
                ResultSet res = stmt.executeQuery(req);
                while(res.next()){
			rep = res.getDouble(1);
		}
                return rep;
        }
        public Object[][] getLCC(String date)throws Exception{
                int nb = 0;
                LigneCredit l = new LigneCredit();
                int[] compte = this.getCompte();
                Object[][] obj = new Object[compte.length][5]; 
                double[] dc = new double[2];
                LigneCredit[] lc = l.getLigneCredit();
                double[] montantEngage = new double[4];
                double[] montantPayer = new double[4];
                double[] montantRestant = new double[4];
                for(int j=0;j<4;j++){
                        montantEngage[j] = getLcEngage(lc[j].code,date);
                        montantPayer[j] = getLcRestant(lc[j].code,date);
                        montantRestant[j] = getLcPayer(lc[j].code,date);
                }
                for(int i = 0;i<lc.length;i++){
                    //dc = this.getSum(compte[i],"09/09/",dateFin);
                    obj[i][0] = lc[i].designation;
                    obj[i][1] = lc[i].montant;
                    obj[i][2] = montantEngage[i];
                    obj[i][3] = montantPayer[i];
                    obj[i][4] = montantRestant[i];
                    
                }
                return obj;
        }
        public int[] getCompte()throws Exception{
                int nb = 0;
                UtilDB db =new UtilDB();
                this.conn = db.getConn();
                String req = "select count(*)  from (select distinct(compte) from ecriture)";	
                Statement stmt = conn.createStatement();
                ResultSet res = stmt.executeQuery(req);
                while(res.next()){
			nb = res.getInt(1);
		}
                int[] compte = new int[nb];
                String req1 = "select distinct(compte) from ecriture";	
                Statement stmt1 = conn.createStatement();
                ResultSet res1 = stmt1.executeQuery(req1);
                int i=0;
                while(res1.next()){
			compte[i] = res1.getInt(1);
                        i++;
		}
                return compte;
        }
        public static void main(String[] args)throws Exception{
                Balance b = new Balance(); 
                Object[][] obj = b.getLCC("12/12/2016");
                /*for(int i=0;i<obj.length;i++){
                    System.out.print(obj[i][0]+"    ");
                    System.out.print(obj[i][1]+"    ");
                    System.out.print(obj[i][2]+"    ");
                    System.out.print(obj[i][3]+"    ");
                    System.out.print(obj[i][4]+"    ");
                    System.out.println();
                }*/
                double t = b.getLcEngage(3,"12/09/2016");
                System.out.println(t);
        }
}
