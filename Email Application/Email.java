import java.util.Scanner;

public class Email{
    private String firstName;
    private String lastName;
    private String password;
    private String department;
    private String email;
    private int mailboxCapacity = 500;
    private String alternateEmail;
    private String companyName = "company.com";

    private int passwordLength = 8;

    //Constructer
    public Email(String fName,String lName){
        this.firstName = fName;
        this.lastName = lName;
        System.out.println("Email: " + this.firstName + " " + this.lastName);
        this.department = setDepartment();
        System.out.println("Department: " + this.department);
        this.password = randomPassword(passwordLength);
        System.out.println("Your password is: " + this.password);

        email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@";
        if (department != ""){
            email = email + department + "." + companyName;
        }else{
            email = email + companyName;
        }
        System.out.println("Your email is: " + this.email);

    }

    private String setDepartment(){
        System.out.print("Department Codes\n1 for Sales\n2 for Development\n3 for Accounting\n0 for none\nEnter department code: ");
        Scanner in = new Scanner(System.in);
        int input = in.nextInt();
        if (input == 1){
            return "Sales";
        }else if (input == 2){
            return "Development";
        }else if (input == 3){
            return "Accounting";
        }else{
            return "";
        }
    }
    //Generate a random password
    private String randomPassword(int length){
        String passwordSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()";
        char[] password = new char[length];
        for (int i = 0;i<length;i++){
            int r = (int)(Math.random() * passwordSet.length());
            password[i] = passwordSet.charAt(r);
        }
        return new String(password);
    }

    public String getInfo(){
        return "Email Info:\n" + "Display Name: " + firstName + " " + lastName + "\n" +
        "Company Email: " + email + "\n" +
        "Mailbox Capacity: " + mailboxCapacity + "\n";
    }

    public void showInfo(){
        System.out.println(getInfo());
    }

    //Setters
    public void setMailboxCapacity(int size){this.mailboxCapacity = size;}
    public void setAlternateEmail(String altEmail){this.alternateEmail = altEmail;}
    public void setPassword(String newPassword){this.password = newPassword;}
    //Getters
    public int getMailboxCapacity(){return this.mailboxCapacity;}
    public String getAlternateEmail(){return this.alternateEmail;}
    public String getPassword(){return this.password;}
}