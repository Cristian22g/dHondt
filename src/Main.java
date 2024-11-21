import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        int votantesTotal = 1564082;
        int votosTotal;
        int totalEscanios = 12;
        
        String[] partidos = {"PP", "PSOE", "VOX", "SUMAR", "P ANIMALISTA", "FRENTE OBRERO", "RECORTES CERO"};

        String[] datosGruposStr = {"GRUPO", "VOTOS", "% CENSO", "% EMITIDOS", "ESCAÑOS"};
        double[][] datosGrupos = new double[7][4];

        double votosEmitidos=0, abstencion=0, votosValidos=0, votosBlanco=0, votosNulos=0;
        boolean bucle = true;

        
        do {
            introducirVotos(partidos, datosGrupos);
            votosTotal = calcularNumeroVotos(datosGrupos);        
            votosBlanco = (double)pedirVotosBlancos();               
            votosNulos = (double)pedirVotosNulos();

            int suma = (int) (votosTotal + votosBlanco + votosNulos);

            if (suma > votantesTotal) {
                System.out.println("Los datos han sido introducidos incorrectamente ya que el número de votos supera el censo actual.");
            }else bucle = false;
        } while (bucle);
        
        votosEmitidos = (double)(votosTotal * 100) / votantesTotal;
        abstencion = (((votantesTotal-votosTotal)*100)/(double)votantesTotal);
        votosBlanco = (votosBlanco*100)/votosTotal;
        votosNulos = (votosNulos*100)/votosTotal;
        votosValidos =((votosTotal-votosNulos)*100)/votosTotal;
        
        String[] actaElectoralStr = {"Votos Emitidos", "Abstención", "Votos Validos", "Votos en blanco", "Votos nulos"};
        double[] actaElectoral = {votosEmitidos, abstencion, votosValidos, votosBlanco, votosNulos};
        imprimirActaElectoral(actaElectoralStr, actaElectoral);
        System.out.println();
  
        calcularCensoGrupos(datosGrupos, votantesTotal);
        calcularEmitidosGrupos(datosGrupos, votosTotal); 


        System.out.println();
        double[][] arrDhondt = generarArrDhondt(datosGrupos, totalEscanios);
        repartirEscanios(arrDhondt, datosGrupos, totalEscanios);
        imprimirArrDhondt(arrDhondt, partidos);
    
        imprimirDatosGrupos(partidos, datosGruposStr, datosGrupos);
    }

    ///////////////////////////////METODOS////////////////////////////////////////////
    public static void introducirVotos(String[] partidos, double[][] datosGrupos){
        for (int i = 0; i < partidos.length; i++) {
            System.out.printf("Introduce los votos de %s:",partidos[i]);
            try {
                datosGrupos[i][0] = scInt();
            } catch (Exception e) {
                i--;
            }
            
        }

    }

    public static void imprimirDatosGrupos(String[] partidos, String[] datosGruposStr, double[][] datosGrupos){
        
        for (int i = 0; i < datosGruposStr.length; i++) {
            System.out.printf("%12s", datosGruposStr[i]);
        }
        System.out.println();
        
        for (int i = 0; i < datosGrupos.length; i++) {
            System.out.printf("%12s", partidos[i]);

            for (int j = 0; j < datosGrupos[i].length; j++) {
                if (j == 0 || j == 3) System.out.printf("%12.0f", datosGrupos[i][j]);
                else System.out.printf("%12.2f", datosGrupos[i][j]);
            }
            System.out.println();
        }
    }

    public static void imprimirActaElectoral(String[] actaElectoralStr, double[] actaElectoral){
        System.out.println("---------ACTA ELECTORAL---------");
        for (int i = 0; i < actaElectoral.length; i++) {
            if (i < 2) System.out.printf("%s %.2f%% sobre el censo", actaElectoralStr[i], actaElectoral[i]);
            else System.out.printf("%s %.2f%% sobre emitidos", actaElectoralStr[i], actaElectoral[i]);  
                    
            System.out.println();
        }
    }
    // ACTA ELECTORAL
    public static int calcularNumeroVotos( double[][] datosGrupos){
        int totalVotos = 0;
        for (int i = 0; i < datosGrupos.length; i++) {
            totalVotos += datosGrupos[i][0];
        }
        return totalVotos;
    }

    public static int pedirVotosBlancos(){
        boolean b = false;
        int n = 0;
        do {
            try {
                System.out.println("Introduce el número total de votos blancos");
                n = scInt();
                b = false;
            } catch (Exception e) {
                b = true;
            }
        } while (b);
        return n;
    }

    public static int pedirVotosNulos(){
        boolean b = false;
        int n = 0;
        do {
            try {
                System.out.println("Introduce el número total de votos nulos");
                n = scInt();
                b = false;
            } catch (Exception e) {
                b = true;
            }
        } while (b);
        return n;
    }
    
    //  CALCULO CENSO Y EMITIDO GRUPOS.
    public static void calcularCensoGrupos(double[][] datosGrupos, int votantesTotal){
        for (int i = 0; i < datosGrupos.length; i++) {
            datosGrupos[i][1] = (datosGrupos[i][0] * 100)/votantesTotal;
        }
    }

    public static void calcularEmitidosGrupos(double[][] datosGrupos, int votosTotal){
        for (int i = 0; i < datosGrupos.length; i++) {
            datosGrupos[i][2] = (datosGrupos[i][0] * 100)/votosTotal;
        }
    }
    
    // ARRAY DE ESCAÑOS

    public static double[][] generarArrDhondt(double[][] datosGrupos, int totalEscanios){
    
    double[][] arr = new double[7][totalEscanios];

    for (int i = 0; i < arr.length; i++) {
        double n = datosGrupos[i][0];
        for (int j = 0; j < arr[i].length; j++) {
            arr[i][j] = n/(j+1);
        }
    }

    return arr;   
    }

    public static void imprimirArrDhondt(double[][] arrDhondt, String[] partidos){

        for (int i = 0; i < partidos.length; i++) {
            System.out.printf("%6s ", partidos[i]);
            for (int j = 0; j < arrDhondt[0].length; j++) {
                System.out.printf(" %.2f ", arrDhondt[i][j]);
            }
            System.out.println();
        }
    }

    public static void repartirEscanios(double[][] arrDhondt, double[][] datosGrupos, int totalEscanios){

        int mayor;
        
        for (int i = 0; i < totalEscanios; i++) {
            mayor = 0;
            for (int g = 1; g < arrDhondt.length; g++) {
                
                double valorActual = arrDhondt[g][(int)datosGrupos[g][3]];
                double valorMayor = arrDhondt[mayor][(int)datosGrupos[mayor][3]];


                if( valorActual > valorMayor) mayor = g;  //COMPARO VALORES DEL ARRAY DHONDT Y ME QUEDO CON EL MAYOR

                else if(valorMayor == valorActual){ // SI ESOS VALORES SON IGUALES COMPARO LOS VOTOS
                
                    mayor = (datosGrupos[g][0] > datosGrupos[mayor][0]) ? g : mayor;
                }
            }
            datosGrupos[mayor][3]++;
        }
    }

    // UTILIDADES REUTILIZACION PROGRAMACION

    public static int scInt(){
        Scanner sc = new Scanner(System.in);
        return sc.nextInt();
    }
}
