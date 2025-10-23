public class SumRandomNumber {
    public static void main(String[] args) {
        int numb1 = (new java.util.Random()).nextInt(900) + 100;
        int numb2 = (new java.util.Random()).nextInt(900) + 100;
        int numb3 = (new java.util.Random()).nextInt(900) + 100;

        System.out.printf("Число 1: %d\nЧисло 2: %d\nЧисло 3: %d\n", numb1,numb2,numb3);
        System.out.println("Сумма первых цифр: " + (numb1/100 + numb2/100 + numb3/100));
    }
}
