package drivers;

import java.util.Scanner;
import domaincontrollers.CtrlDomain;
import persistence.CtrlPersistence;

public class Driver {
    private Scanner reader;
    private CtrlDomain ctrlDomain;
    private CtrlPersistence ctrlPersistence;

    public Driver() {
        reader = new Scanner(System.in);
        ctrlPersistence = CtrlPersistence.getInstance();
        ctrlDomain = CtrlDomain.getInstance();
    }

    // /**
    //  * @throws InputMismatchException (ara no)
    //  */
    // public void createUser() {
    //     System.out.println("Enter username: ");
    //     String name, password, passwordConfirmation, email;
    //     try {
    //         name = reader.nextLine();
    //         System.out.println("Enter email: ");
    //         email = reader.nextLine();
    //     } catch (InputMismatchException exception) {
    //         System.out.println("Exception: Please enter a valid fomat");
    //         System.out.println("Retry:");
    //         createUser();
    //     }
    //     boolean passwordSet = false;
    //     while (!passwordSet) {
    //         try {
    //             System.out.println("Enter password: ");
    //             password = reader.nextLine();
    //             System.out.println("Enter password to confirm: ");
    //             passwordConfirmation = reader.nextLine();            
    //         } catch (IllegalArgumentException exception) {
    //             System.out.println("Exception: Please enter a valid fomat");
    //             System.out.println("Retry:");
    //         }
    //     }
    //     Integer idUser;
    //     try {
    //         idUser = ctrlDomain.createUser(idUser, name);
    //     } catch (IllegalArgumentException exception) {
    //         System.out.println("Exception: " + exception.getMesage());
    //         System.out.println("Retry:");
    //         createUser();
    //     }
    //     registeredUsrLogedIn(idUser);
    // }
    //
    // public void registeredUsrLogedIn(Integer idUser) {
    //     System.out.println("0 -> Create new form");
    //     System.out.println("1 -> Open form");
    //     int option;
    //     while (true) {
    //         try {
    //             option = reader.nextInt();
    //         } catch (InputMismatchException exception) {
    //             System.out.println("Exception: Please enter a valid fomat");
    //             System.out.println("Retry:");
    //         }
    //         if (option == 0) createForm(idUser);
    //         else if (option == 1) openForm(idUser);
    //         else {
    //             System.out.println("Please enter a valid number");
    //             System.out.println("Retry:");
    //         }
    //     }
    // }
    //
    // public void createForm(Integer idUser) {
    //     boolean nameSet = false;
    //     String name;
    //     while (!nameSet) {
    //         try {
    //             System.out.println("Enter form name: ");
    //             name = reader.nextLine();
    //         } catch (IllegalArgumentException exception) {
    //             System.out.println("Exception: Please enter a valid fomat");
    //             System.out.println("Retry:");
    //         }
    //     }
    //     try {
    //         Integer idForm = ctrlDomain.createForm(idUser, name);
    //     } catch (IllegalArgumentException exception) {
    //         System.out.println("Exception: " + exception.getMesage());
    //         System.out.println("Retry:");
    //         createForm(idUser);
    //     }
    //     openForm(idForm);
    // }
    //
    // // ---------------------- Funcions per a domain controler ----------------------------
    // /*
    // public ArrayList<Integer> getIdForms(Integer idUser) {
    //     ArrayList<Integer> result;
    //     RegisteredUser user = getRegisteredUser(idUser);
    //     result = user.getForms();
    //     return result;
    // }
    //
    // public ArrayList<String> getNameForms(Integer idUser) {
    //     ArrayList<String> result = new ArrayList<>();
    //     ArrayList<Integer> ids = new ArrayList<>();
    //     // try {
    //         RegisteredUser user = getRegisteredUser(idUser);
    //         ids = user.getForms();
    //     // } catch (IllegalArgumentException exception) {
    //     //     System.out.println("Exception: " + exception.getMessage());
    //     // }
    //     int i;
    //     int nForms = ids.size();
    //     // try {
    //         for (i = 0; i < nForms; i++) {
    //             Form form = getForm(ids.get(i));
    //             result.add(form.getName());
    //         }
    //     // } catch (EntityNotFoundException e) {
    //     //     System.out.println
    //
    //     return result;
    // }
    // */
    //
    // public void openForm(Integer idUser) {
    //     System.out.println("Select form to open:");
    //     System.out.println("Id -> Name");
    //     // ------------------ Necessitariem aquestes 2 funcions ----------------------
    //     //  |
    //     //  v
    //     try {
    //         ArrayList<Integer> idsForms = ctrlDomain.getIdForms(idUser);
    //         ArrayList<String> nameForms = ctrlDomain.getNameForms(idUser);
    //     } catch (Exception e) {
    //         System.out.println("Exception: " + exception.getMessage());
    //     }
    //
    //     int nForms = idsFroms.size();
    //     for (int i = 0; i < nForms; i++) {
    //         System.out.println(idForms.get(i) + " -> " + nameForms.get(i));
    //     }
    //     // no afegim comprovació de que el valor introduït és dins d'un rang pq els ids no han de pq ser consecutius
    //     Integer idForm = reader.nextInt();
    //     // Teoritzant sobre com funciona importForm
    //     // Treballem amb un Form, q esta mal, pero es placeholder
    //     Form = importform(idForm);
    //     showQuestions();
    //     System.out.println("Choose option:");
    //     System.out.println("0 -> Edit form");
    //     System.out.println("1 -> Answer form");
    //     int option;
    //     while (true) {
    //         try {
    //             option = reader.nextInt();
    //             if (option == 0) editForm();
    //             else if (option == 1) answerForm();
    //             else {
    //                 System.out.println("Please enter a valid number");
    //                 System.out.println("Retry:");
    //             }
    //         } catch (InputMismatchException exception) {
    //             System.out.println("Exception: Please enter a valid fomat");
    //             System.out.println("Retry:");
    //         }
    //     }
    // }
    //
    // // Given a form it prints all its questions (statement + answers) in order
    // public void showQuestions(Form form) {
    //     ArrayList<Integer> idQuestions = Form.getQuestions();
    //     ArrayList<Question> questions = new ArrayList<> ();
    //     int nQuestions = idQuestions.size();
    //     for (int i = 0; i < nQuestions; i++) {
    //         System.out.println("Question " + i + ": ");
    //         System.out.println(questions.get(i).getStatement());
    //         String kindQuestion = questions.get(i).getClass().getName();
    //         System.out.println("Type of question: " + kindQuestion);
    //         if (kindQuestion == "OrderedSingleQuestion") {
    //             ArrayList<String> options = questions.get(i).getOptions();
    //             System.out.println("Options: ");
    //             int nOptions = options.size();
    //             for (int j = 0; j < nOptions; j++) {
    //                 System.out.println(j + " -> " + options.get(i));
    //             }
    //         } else if (kindQuestion == "NumericalQuestion") {
    //             System.out.println("Max = " + question.get(i).getMax() + "Min = " + question.get(i).getMin());
    //         } else if (kindQuestion == "MultipleChoiceQuestion") {
    //             ArrayList<String> options = questions.get(i).getOptions();
    //             System.out.println("Options: ");
    //             int nOptions = options.size();
    //             for (int j = 0; j < nOptions; j++) {
    //                 System.out.println(options.get(i));
    //             }
    //         } else if (kindQuestion == "FreeQuestion") {
    //         }
    //         System.out.println("");
    //     }
    // }
    //
    // public void enterUser() {
    //     System.out.println("Choose option:");
    //     System.out.println("0 -> Anonymus User");
    //     System.out.println("1 -> Log in with registered user");
    //
    //     int option;
    //     while (true) {
    //         try {
    //             option = reader.nextInt();
    //             if (option == 0) enterAnonymus();
    //             else if (option == 1) logIn();
    //             else {
    //                 System.out.println("Please enter a valid number");
    //                 System.out.println("Retry:");
    //             }
    //         } catch (InputMismatchException exception) {
    //             System.out.println("Exception: Please enter a valid fomat");
    //             System.out.println("Retry:");
    //         }
    //
    //     }
    // }
    //
    // // ---- TODO: afegir gestio excepcions
    // public void enterAnonymus() {
    //     Integer idUser = ctrlDomain.createAnonymousUser();
    //     System.out.println("Select form to answer:");
    // }  
    //
    // // De moment no fa res pq sense controlador de persistencia complet no te sentit
    // public void exit() {
    //     System.out.println("Exiting aplication...");
    //     System.exit(0);
    // }
    //
    //
    // public static void main(String[] args) {
    //     System.out.println("Choose option:");
    //     System.out.println("0 -> Create User");
    //     System.out.println("1 -> Enter User (Registered or anonymus)");
    //     System.out.println("2 -> Exit");
    //
    //     int option;
    //     boolean exit = false;
    //     while (!exit) {
    //         try {
    //             option = reader.nextInt();
    //             if (option == 0) createUser();
    //             else if (option == 1) enterUser();
    //             else if (option == 2) exit = true;
    //             else {
    //                 System.out.println("Please enter a valid number");
    //                 System.out.println("Retry:");
    //             }
    //         } catch (InputMismatchException exception) {
    //             System.out.println("Exception: Please enter a valid fomat");
    //             System.out.println("Retry:");
    //         }
    //     }
    //     exit();
    // }
}

