/**
 * Created by Erin on 3/11/2018.
 */
public class ReturnTreeStruct {

    public double value;
    public boolean isInt;

    public ReturnTreeStruct() {
        this.value = 0.0;
        this.isInt = true;
    }


    public static double Multiply(double a, double b){
        return (double)a * (double)b;
    }

    public static int Multiply(int a, int b){
        return (int)a * (int)b;
    }

    public static double Divide(double a, double b){
        return (double)a / (double)b;
    }

    public static int Divide(int a, int b){
        return (int)a / (int)b;
    }

    public static double Add(double a, double b){
        return (double)a + (double)b;
    }

    public static int Add(int a, int b){
        return (int)a + (int)b;
    }

    public static double Sub(double a, double b){
        return (double)a - (double)b;
    }

    public static int Sub(int a, int b){
        return (int)a - (int)b;
    }
}
