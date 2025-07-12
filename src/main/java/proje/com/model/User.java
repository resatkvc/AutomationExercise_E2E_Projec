// Testlerde kullanılacak kullanıcı ve kart bilgilerini tutan model sınıfı.
// Tüm alanlar public, kolay veri aktarımı için.
package proje.com.model;

public class User {
    // Kullanıcı başlığı (Mr/Mrs)
    public String title;
    // Kullanıcı adı
    public String name;
    // Kullanıcı email adresi
    public String email;
    // Kullanıcı şifresi
    public String password;
    // Doğum günü bilgileri
    public String birthDay;
    public String birthMonth;
    public String birthYear;
    // Kişisel bilgiler
    public String firstName;
    public String lastName;
    public String company;
    public String address1;
    public String address2;
    public String country;
    public String state;
    public String city;
    public String zipcode;
    public String mobileNumber;
    // Kart bilgileri
    public String cardName;
    public String cardNumber;
    public String cvc;
    public String expMonth;
    public String expYear;
} 