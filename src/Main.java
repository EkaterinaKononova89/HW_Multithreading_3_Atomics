import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    static AtomicInteger length3 = new AtomicInteger(0);
    static AtomicInteger length4 = new AtomicInteger(0);
    static AtomicInteger length5 = new AtomicInteger(0);


    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        String[] texts = new String[100_000];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("abc", 3 + random.nextInt(3));
        }

        // 1й поток - читается одинаково как слева направо, так и справа налево (палиндром)
        Thread thread1 = new Thread(() -> {
            for (String text : texts) {
                StringBuilder textReverse = new StringBuilder();
                for (int t = text.length() - 1; t >= 0; t--) {
                    textReverse.append(text.charAt(t));
                }
                if (text.contentEquals(textReverse) && !exactlySameLetters(text)) {
                    choseLength(text);
                }
            }
        });
        thread1.start();

        //2й поток - слово состоит из одной и той же буквы
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < texts.length; i++) {
                if (exactlySameLetters(texts[i])) {
                    choseLength(texts[i]);
                }
            }
        });
        thread2.start();

        //3й поток - буквы в слове идут по возрастанию
        Thread thread3 = new Thread(() -> {
            for (String text : texts) {
                for (int j = 0; j < text.length() - 1; j++) { // прохожусь по слову посимвольно
                    if (text.charAt(j) <= text.charAt(j + 1)) { // проверка двух последовательных символов
                        if (j + 1 == text.length() - 1) { // проверка является ли этот символ крайним в слове
                            if (!exactlySameLetters(text)) {
                                choseLength(text);
                            }
                        }
                    } else {
                        break;
                    }
                }
            }
        });
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();

        System.out.println("Красивых слов с длиной 3: " + length3);
        System.out.println("Красивых слов с длиной 4: " + length4);
        System.out.println("Красивых слов с длиной 5: " + length5);
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static boolean exactlySameLetters(String wordFromArr) {
        for (int c = 1; c < wordFromArr.length(); c++) {
            if (wordFromArr.charAt(0) != wordFromArr.charAt(c)) {
                return false;
            }
        }
        return true;
    }

    public static void choseLength(String wordFromArr) {
        switch (wordFromArr.length()) {
            case 3:
                length3.getAndIncrement();
                break;
            case 4:
                length4.getAndIncrement();
                break;
            case 5:
                length5.getAndIncrement();//incrementAndGet(); в данном случае есть разница, какой метод использовать?
                break;
            default:
                System.out.println("Неверная длина");
        }
    }
}

