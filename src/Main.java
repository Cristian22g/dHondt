import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        int votantesTotal;
        int votosTotal;
        int totalEscanios;
        
        // SE PIDEN LOS DATOS AL USUARIO

        System.out.println("Cúal es el censo total?");
        votantesTotal = scInt();
        
        System.out.println("Cúantos escaños se van a repartir?");
        totalEscanios = scInt();
     
        System.out.println("¿Cuántos grupos hay?");
        int numPartidos = scInt();

        // SE CREAN LOS ARRAYS QUE AYUDARAN PARA GUARDAR LA INFORMACIÓN DE LOS PARTIDOS E IMPRIMIRLAS

        String[] nombresPartidos = new String[numPartidos];
        String[] datosGruposStr = {"GRUPO", "VOTOS", "% CENSO", "% EMITIDOS", "ESCAÑOS"};
        double[][] datosGrupos = new double[nombresPartidos.length][4];


        
        double votosEmitidos=0, abstencion=0, votosValidos=0, votosBlanco=0, votosNulos=0;
        boolean bucle = true;


        // SE PIDEN LOS NOMBRES DE LOS PARTIDOS
        for (int i = 0; i < nombresPartidos.length; i++) {
            System.out.println("Introduce el nombre de un partido:");          
            try {
                nombresPartidos[i] = new Scanner(System.in).nextLine();
            } catch (Exception e) {
                System.out.println("Ha habido un error en la introducción del nombre, introduzcalo de nuevo:");
                i--;
            }
        }

        
        // SE PIDE EL NUMERO DE VOTOS DE CADA PARTIDO, VOTOS EN BLANCO Y NULOS, SI LA SUMA DE LA CANTIDAD DE VOTOS INTRODUCIDA
        // SUPERA EL CENSO, SE VOLVERAN A PREGUNTAR LOS VOTOS.
        do {
            introducirVotos(nombresPartidos, datosGrupos);
            votosTotal = calcularNumeroVotos(datosGrupos);
            System.out.println("Introduce los números de votos en blanco:");       
            votosBlanco = scInt();
            System.out.println("Introduce los números de votos nulos:");             
            votosNulos = scInt();

            int suma = (int) (votosTotal + votosBlanco + votosNulos);

            if (suma > votantesTotal) {
                System.out.println("Los datos han sido introducidos incorrectamente ya que el número de votos supera el censo actual.");
            }else bucle = false;
        } while (bucle);
        

        // OPERACIONES PARA SACAR PORCENTAJES DE LA TABLA ACTA ELECTORAL
        votosEmitidos = (double)(votosTotal * 100) / votantesTotal;
        abstencion = (((votantesTotal-votosTotal)*100)/(double)votantesTotal);
        votosBlanco = (votosBlanco*100)/votosTotal;
        votosNulos = (votosNulos*100)/votosTotal;
        votosValidos =((votosTotal-votosNulos)*100)/votosTotal;
        
        // ARRAYS PARA IMPRIMIR EL ACTA ELECTORAL Y LLAMADA AL METODO QUE LO IMPRIME.
        String[] actaElectoralStr = {"Votos Emitidos", "Abstención", "Votos Validos", "Votos en blanco", "Votos nulos"};
        double[] actaElectoral = {votosEmitidos, abstencion, votosValidos, votosBlanco, votosNulos};
        imprimirActaElectoral(actaElectoralStr, actaElectoral);
        System.out.println();
  

        // METODOS QUE CALCULAN PORCENTAJE DE CENSO Y EMITIDOS DE CADA PARTIDO
        calcularCensoGrupos(datosGrupos, votantesTotal);
        calcularEmitidosGrupos(datosGrupos, votosTotal); 


        System.out.println();
        double[][] arrDhondt = generarArrDhondt(datosGrupos, totalEscanios);
        repartirEscanios(arrDhondt, datosGrupos, totalEscanios);
            //imprimirArrDhondt(arrDhondt, nombresPartidos);
    
        imprimirDatosGrupos(nombresPartidos, datosGruposStr, datosGrupos);
    }

    ///////////////////////////////METODOS////////////////////////////////////////////
    public static void introducirVotos(String[] partidos, double[][] datosGrupos){
        for (int i = 0; i < partidos.length; i++) {
            System.out.printf("Introduce los votos de %s:",partidos[i]);
                datosGrupos[i][0] = scInt();
            
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

    public static int calcularNumeroVotos( double[][] datosGrupos){
        int totalVotos = 0;
        for (int i = 0; i < datosGrupos.length; i++) {
            totalVotos += datosGrupos[i][0];
        }
        return totalVotos;
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
    
    double[][] arr = new double[datosGrupos.length][totalEscanios];

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

        int posPartidoMasVotos;
        int[] posPartidosArrDhont = new int[datosGrupos.length]; // AQUI SE GUARDARAN EL NÚMERO DE ESCAÑOS QUE LLEVA CADA GRUPO Y ASI PODER COMPARAR EL SIGUIENTE
                                                                 // VALOR DEL ARRAY DHONT
        
        for (int i = 0; i < totalEscanios; i++) {
            posPartidoMasVotos = 0; // POSICION DEL PARTIDO CON MAS VOTOS, INICIADO EN 0.
            for (int g = 1; g < arrDhondt.length; g++) {
                //EL BUBLE EN SU PRIMERA ITERACION COMPARA LOS VOTOS DEL PARTIDO 1 CON EL VALOR MAYOR, QUE SERIA EL PARTIDO EN LA POSICIÓN 0, 
                //Y ASI COMPARANDO SUCESIVAMENTE EL SIGUIENTE CON EL MAYOR QUE SE GUARDA EN 'posPartidoMasVotos'
                
                double valorActual = arrDhondt[g][posPartidosArrDhont[g]]; 
                double valorMayor = arrDhondt[posPartidoMasVotos][(int)posPartidosArrDhont[posPartidoMasVotos]];

                if( valorActual > valorMayor) posPartidoMasVotos = g;  //COMPARO VALORES DEL ARRAY DHONDT Y ME QUEDO CON EL MAYOR

                else if(valorMayor == valorActual){ // SI ESOS VALORES SON IGUALES COMPARO LOS VOTOS
                    
                    posPartidoMasVotos = (posPartidosArrDhont[g] > posPartidosArrDhont[posPartidoMasVotos]) ? g : posPartidoMasVotos; // SI LOS VALORES SON IGUALES, COMPARA LOS ESCAÑOS DE CADA GRUPO
                }
            }
            datosGrupos[posPartidoMasVotos][3]++; // SE LE SUMA UN ESCAÑO AL MAYOR
            posPartidosArrDhont[posPartidoMasVotos]++; // SE LE SUMA UN ESCAÑO AL MAYOR
        }
    }

    // UTILIDADES REUTILIZACION

    public static int scInt(){
        int n = 0;
        
        boolean repeat = false;
        do {
            try {
                Scanner sc = new Scanner(System.in);
                n = sc.nextInt();
                repeat = false;
            } catch (Exception e) {
                System.out.println("Ha ocurrido un error inesperado, introduce el número de nuevo (solo números, sin puntos ni comas)");
                repeat = true;
            }
        } while (repeat);
        
        return n;
    }
}
