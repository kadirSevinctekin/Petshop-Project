public class Main {
    public static void main(String[] args) {
        DBConnect database = new DBConnect() ;
        database.connectToDb("Petshop", "postgres" , "1234") ;
        firstPage firstPage = new firstPage() ;
    }
}
