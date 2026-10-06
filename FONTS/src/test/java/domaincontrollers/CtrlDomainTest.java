package domaincontrollers;

import domain.*;
import domain.AnalysisResult;
import org.junit.*;
import org.mockito.*;

import persistence.CtrlPersistence;
import utils.DomainException;
import utils.EntityNotFoundException;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Unit test for the class CtrlDomain.
 *
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class CtrlDomainTest {

    private CtrlDomain ctrlDomain;
    @Mock private CtrlPersistence mockPersistence;
    @Mock private CtrlTransformData mockTransform;

    private AutoCloseable mocks;

    @Before
    public void setUp() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);

        // Reset singleton
        Field instanceField = CtrlDomain.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        ctrlDomain = CtrlDomain.getInstance();

        // Inject mocks
        Field persistenceField = CtrlDomain.class.getDeclaredField("_ctrlPersistence");
        persistenceField.setAccessible(true);
        persistenceField.set(ctrlDomain, mockPersistence);

        Field transformField = CtrlDomain.class.getDeclaredField("_ctrlTransformData");
        transformField.setAccessible(true);
        transformField.set(ctrlDomain, mockTransform);
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    private void mockVoidDeletes() {
        doNothing().when(mockPersistence).deleteAnswerToQuestion(anyInt(), anyInt(), anyInt());
        doNothing().when(mockPersistence).deleteAnswer(anyInt(), anyInt());
        doNothing().when(mockPersistence).deleteQuestion(anyInt(), anyInt());
        doNothing().when(mockPersistence).deleteAnalyze(anyInt(), anyInt());
        doNothing().when(mockPersistence).deleteForm(anyInt());
        doNothing().when(mockPersistence).deleteUser(anyInt());
    }

    @Test
    public void testCreateUserSuccess() throws Exception {
        when(mockPersistence.addUser(any(User.class))).thenReturn(10);

        Integer id = ctrlDomain.createUser("john", "pass1234", "pass1234", "john@mail.com");

        assertEquals(Integer.valueOf(10), id);
        verify(mockPersistence, times(1)).addUser(any(User.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserPasswordMismatch() {
        ctrlDomain.createUser("john", "abc", "xyz", "john@mail.com");
    }

    @Test
    public void testCreateAnonymousUser() throws Exception {
        when(mockPersistence.addUser(any(User.class))).thenReturn(5);

        Integer id = ctrlDomain.createAnonymousUser();

        assertEquals(Integer.valueOf(5), id);
        verify(mockPersistence).addUser(any(AnonymousUser.class));
    }

    @Test
    public void testLoginUserSuccess() throws Exception {
        RegisteredUser ru = new RegisteredUser(7, "john", "pass1234", "john@mail.com");

        when(mockPersistence.getUser(7)).thenReturn(ru);

        ctrlDomain.loginUser(7, "pass1234");

        assertEquals(Integer.valueOf(7), ctrlDomain.getLoggedUser());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoginUserWrongPassword() throws Exception {
        RegisteredUser ru = new RegisteredUser(7, "john", "pass1234", "john@mail.com");

        when(mockPersistence.getUser(7)).thenReturn(ru);

        ctrlDomain.loginUser(7, "wrong");
    }

    @Test(expected = DomainException.class)
    public void testLogoutUser() throws Exception {
        RegisteredUser ru = new RegisteredUser(7, "john", "pass1234", "john@mail.com");
        when(mockPersistence.getUser(7)).thenReturn(ru);

        ctrlDomain.loginUser(7, "pass1234");
        ctrlDomain.logoutUser();

        ctrlDomain.getLoggedUser();
    }

    @Test
    public void testCreateFormSuccess() throws Exception {
        RegisteredUser ru = new RegisteredUser(3, "anna", "pass1234", "a@mail.com");
        ru.setForms(new ArrayList<>());

        when(mockPersistence.getUser(3)).thenReturn(ru);
        when(mockPersistence.addForm(any(Form.class))).thenReturn(20);

        Integer idForm = ctrlDomain.createForm(3, "Survey A");

        assertEquals(Integer.valueOf(20), idForm);
        verify(mockPersistence).addForm(any(Form.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateFormFailsIfUserNotRegistered() throws Exception {
        when(mockPersistence.getUser(3)).thenReturn(new AnonymousUser(3));
        ctrlDomain.createForm(3, "Survey A");
    }

    @Test
    public void testPublishForm() throws Exception {
        RegisteredUser ru = new RegisteredUser(3, "anna", "pass1234", "a@mail.com");
        Form f = new Form(10, "Survey", 3);

        when(mockPersistence.getUser(3)).thenReturn(ru);
        when(mockPersistence.getForm(10)).thenReturn(f);

        ctrlDomain.loginUser(3, "pass1234");
        ctrlDomain.publishForm(10);

        assertTrue(f.isPublished());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPublishFormNotOwner() throws Exception {
        RegisteredUser ru = new RegisteredUser(3, "anna", "pass1234", "a@mail.com");
        Form f = new Form(10, "Survey", 99);

        when(mockPersistence.getUser(3)).thenReturn(ru);
        when(mockPersistence.getForm(10)).thenReturn(f);

        ctrlDomain.loginUser(3, "pass1234");
        ctrlDomain.publishForm(10);
    }

    @Test
    public void testAddNumericalQuestion() throws Exception {
        RegisteredUser ru = new RegisteredUser(3, "anna", "pass1234", "a@mail.com");
        Form f = new Form(10, "Survey", 3);

        when(mockPersistence.getUser(3)).thenReturn(ru);
        when(mockPersistence.getForm(10)).thenReturn(f);
        when(mockPersistence.addQuestion(any(Question.class))).thenReturn(100);

        ctrlDomain.loginUser(3, "pass1234");
        Integer idQ = ctrlDomain.addNumericalQuestion(10, "Age?", false, 0, 120);

        assertEquals(Integer.valueOf(100), idQ);
    }

    @Test
    public void testAddNumericalAnswerCreatesAnswerIfMissing() throws Exception {
        Integer idForm = 10;
        Integer idUser = 7;
        Integer idQuestion = 2;
        Integer numericalAnswer = 42;

        Form f = new Form(idForm, "Survey", 3);
        when(mockPersistence.getForm(idForm)).thenReturn(f);
        
        RegisteredUser u7 = new RegisteredUser(idUser, "john", "pass1234", "john@mail.com");
        when(mockPersistence.getUser(idUser)).thenReturn(u7);

        Answer created = new Answer(idUser, idForm);
        // getAnswer should throw EntityNotFoundException
        when(mockPersistence.getAnswer(idUser, idForm))
                .thenThrow(new EntityNotFoundException("not found"))
                .thenReturn(created);

        // addAnswer and addAnswerToQuestion are void in persistence; domain addAnswer/addAnswerToQuestion wrap them
        doNothing().when(mockPersistence).addAnswer(any(Answer.class));
        doNothing().when(mockPersistence).addAnswerToQuestion(any(AnswerToQuestion.class));

        ctrlDomain.addNumericalAnswer(idUser, idForm, idQuestion, numericalAnswer);

        verify(mockPersistence).addAnswer(any(Answer.class));
        verify(mockPersistence).addAnswerToQuestion(any(AnswerToQuestion.class));
    }

    @Test
    public void testCloseCallsPersistence() throws Exception {
        ctrlDomain.close();
        verify(mockPersistence).close();
    }

    @Test
    public void testModifyFormNameSuccess() throws Exception {
        RegisteredUser ru = new RegisteredUser(3, "anna", "pass1234", "a@mail.com");
        Form f = new Form(10, "OldName", 3);

        when(mockPersistence.getUser(3)).thenReturn(ru);
        when(mockPersistence.getForm(10)).thenReturn(f);

        ctrlDomain.loginUser(3, "pass1234");
        ctrlDomain.modifyFormName(10, "NewName");

        assertEquals("NewName", f.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testModifyFormNameNotOwner() throws Exception {
        RegisteredUser ru = new RegisteredUser(3, "anna", "pass1234", "a@mail.com");
        Form f = new Form(10, "OldName", 99);

        when(mockPersistence.getUser(3)).thenReturn(ru);
        when(mockPersistence.getForm(10)).thenReturn(f);

        ctrlDomain.loginUser(3, "pass1234");
        ctrlDomain.modifyFormName(10, "NewName");
    }

    @Test
    public void testReorderFormQuestionsSuccess() throws Exception {
        RegisteredUser ru = new RegisteredUser(3, "anna", "pass1234", "a@mail.com");
        Form f = new Form(10, "Survey", 3);
        f.setQuestions(new ArrayList<>(Arrays.asList(1, 2, 3)));

        when(mockPersistence.getUser(3)).thenReturn(ru);
        when(mockPersistence.getForm(10)).thenReturn(f);

        ctrlDomain.loginUser(3, "pass1234");
        ctrlDomain.reorderFormQuestions(10, Arrays.asList(3, 1, 2));

        assertEquals(Arrays.asList(3, 1, 2), f.getQuestions());
    }

    @Test
    public void testModifyNumericalQuestionSuccess() throws Exception {
        RegisteredUser ru = new RegisteredUser(3, "anna", "pass1234", "a@mail.com");
        Form f = new Form(10, "Survey", 3);
        NumericalQuestion nq = new NumericalQuestion(1, "Age?", false, 0, 100);
        nq.setIdForm(10);

        when(mockPersistence.getUser(3)).thenReturn(ru);
        when(mockPersistence.getForm(10)).thenReturn(f);
        when(mockPersistence.getQuestion(10, 1)).thenReturn(nq);

        ctrlDomain.loginUser(3, "pass1234");
        ctrlDomain.modifyNumericalQuestion(10, 1, "NewAge?", true, 5, 120);

        assertEquals("NewAge?", nq.getStatement());
        assertTrue(nq.isOptional());
        assertEquals(Integer.valueOf(5), nq.getMinValue());
        assertEquals(Integer.valueOf(120), nq.getMaxValue());
    }

    @Test
    public void testModifyFreeAnswerSuccess() throws Exception {
        AnswerFreeQuestion afq = new AnswerFreeQuestion(7, 10, 1, "old");
        when(mockPersistence.getAnswerToQuestion(7, 10, 1)).thenReturn(afq);

        // Logged user = 7
        RegisteredUser ru = new RegisteredUser(7, "john", "pass1234", "j@mail.com");
        when(mockPersistence.getUser(7)).thenReturn(ru);
        ctrlDomain.loginUser(7, "pass1234");

        ctrlDomain.modifyFreeAnswer(7, 10, 1, "new");

        assertEquals("new", afq.getAnswerText());
    }

    @Test
    public void testGetMultipleChoiceAnswer() throws Exception {
        AnswerMultipleChoiceQuestion amcq =
                new AnswerMultipleChoiceQuestion(7, 10, 1, new ArrayList<>(Arrays.asList(2, 4)));

        when(mockPersistence.getAnswerToQuestion(7, 10, 1)).thenReturn(amcq);

        RegisteredUser ru = new RegisteredUser(7, "john", "pass1234", "j@mail.com");
        when(mockPersistence.getUser(7)).thenReturn(ru);
        ctrlDomain.loginUser(7, "pass1234");

        ArrayList<Integer> result = ctrlDomain.getMultipleChoiceAnswer(10, 1);

        assertEquals(Arrays.asList(2, 4), result);
    }

    @Test
    public void testGetAnalysisQuality() throws Exception {
        Analyze analysis = mock(Analyze.class);
        AnalysisResult result = mock(AnalysisResult.class);

        when(mockPersistence.getAnalyze(10, 50)).thenReturn(analysis);
        when(analysis.getResult()).thenReturn(result);
        when(result.getQuality()).thenReturn(0.87);

        double q = ctrlDomain.getAnalysisQuality(50, 10);

        assertEquals(0.87, q, 0.0001);
    }

    @Test
    public void testGetAnalysisAssignations() throws Exception {
        Analyze analysis = mock(Analyze.class);
        AnalysisResult result = mock(AnalysisResult.class);

        when(mockPersistence.getAnalyze(10, 50)).thenReturn(analysis);
        when(analysis.getResult()).thenReturn(result);
        when(result.getClusterAssignments()).thenReturn(new int[]{1, 0, 1});

        int[] arr = ctrlDomain.getAnalysisAssignations(50, 10);

        assertArrayEquals(new int[]{1, 0, 1}, arr);
    }

    @Test
    public void testDeleteAnswerToQuestionSuccess() throws Exception {
        // Meaningful variable names
        final int formId = 10;
        final int userId = 7;
        final int questionId = 1;

        // Mock Form 
        Form form = new Form(formId, "Test Form", userId);
        form.addAnswer(userId);   // The form must know this user answered
        when(mockPersistence.getForm(formId)).thenReturn(form);

        // Mock User 
        RegisteredUser user = new RegisteredUser(userId, "john", "pass1234", "john@mail.com");
        user.addAnswer(formId);   // User must know they answered this form
        when(mockPersistence.getUser(userId)).thenReturn(user);

        // Mock Answer
        Answer answer = new Answer(formId, userId);
        answer.addResponse(questionId);
        when(mockPersistence.getAnswer(userId, formId)).thenReturn(answer);

        // Mock AnswerToQuestion
        AnswerToQuestion atq = new AnswerNumericalQuestion(userId, formId, questionId, 42);
        when(mockPersistence.getAnswerToQuestion(userId, formId, questionId)).thenReturn(atq);

        // Mock void deletes
        mockVoidDeletes();

        // Execute
        ctrlDomain.deleteAnswerToQuestion(userId, formId, questionId);

        // Verify
        verify(mockPersistence).deleteAnswerToQuestion(userId, formId, questionId);
        assertTrue(answer.getResponses().isEmpty());
        assertFalse(form.getAnswers().contains(userId));   // deleteAnswer should remove it
        assertFalse(user.getAnswers().contains(formId));   // user should no longer list this answer
    }

    @Test
    public void testDeleteQuestionRemovesAllAnswerToQuestions() throws Exception {
        final int formId = 10;
        final int ownerId = 3;
        final int userA = 7;
        final int userB = 8;
        final int questionId = 1;

        // Mock Form
        Form form = new Form(formId, "Survey", ownerId);
        form.addAnswer(userA);
        form.addAnswer(userB);
        when(mockPersistence.getForm(formId)).thenReturn(form);

        // Mock Users
        RegisteredUser ruA = new RegisteredUser(userA, "alice", "pass1234", "alice@mail.com");
        ruA.addAnswer(formId);

        RegisteredUser ruB = new RegisteredUser(userB, "bob", "pass1234", "bob@mail.com");
        ruB.addAnswer(formId);

        when(mockPersistence.getUser(userA)).thenReturn(ruA);
        when(mockPersistence.getUser(userB)).thenReturn(ruB);

        // Mock Answers
        Answer ansA = new Answer(formId, userA);
        ansA.addResponse(questionId);

        Answer ansB = new Answer(formId, userB);
        ansB.addResponse(questionId);

        when(mockPersistence.getAnswer(userA, formId)).thenReturn(ansA);
        when(mockPersistence.getAnswer(userB, formId)).thenReturn(ansB);

        // Mock AnswerToQuestion
        when(mockPersistence.getAnswerToQuestion(userA, formId, questionId))
                .thenReturn(new AnswerNumericalQuestion(userA, formId, questionId, 5));

        when(mockPersistence.getAnswerToQuestion(userB, formId, questionId))
                .thenReturn(new AnswerNumericalQuestion(userB, formId, questionId, 6));

        // Mock void deletes
        mockVoidDeletes();

        // Execute
        ctrlDomain.deleteQuestion(formId, questionId);

        // Verify persistence calls
        verify(mockPersistence).deleteAnswerToQuestion(userA, formId, questionId);
        verify(mockPersistence).deleteAnswerToQuestion(userB, formId, questionId);
        verify(mockPersistence).deleteQuestion(formId, questionId);

        // Verify domain state updates
        assertFalse(ansA.getResponses().contains(questionId));
        assertFalse(ansB.getResponses().contains(questionId));

        assertFalse(form.getAnswers().contains(userA));
        assertFalse(form.getAnswers().contains(userB));

        assertFalse(ruA.getAnswers().contains(formId));
        assertFalse(ruB.getAnswers().contains(formId));
    }


    @Test
    public void testDeleteFormCascade() throws Exception {
        final int formId = 10;
        final int ownerId = 3;
        final int answeringUserId = 7;
        final int question1 = 1;
        final int question2 = 2;
        final int analyzeId = 50;

        // Form and owner
        Form form = new Form(formId, "Survey", ownerId);
        form.addQuestion(question1);
        form.addQuestion(question2);
        form.addAnswer(answeringUserId);
        form.addAnalyze(analyzeId);

        RegisteredUser owner = new RegisteredUser(ownerId, "anna", "pass1234", "a@mail.com");
        owner.addForm(formId);

        when(mockPersistence.getForm(formId)).thenReturn(form);
        when(mockPersistence.getUser(ownerId)).thenReturn(owner);

        // Mock answering user (REQUIRED to avoid NPE)
        RegisteredUser answeringUser = new RegisteredUser(answeringUserId, "bob", "pass1234", "b@mail.com");
        answeringUser.addAnswer(formId);
        when(mockPersistence.getUser(answeringUserId)).thenReturn(answeringUser);

        // Mock Answer for answering user
        Answer answer = new Answer(formId, answeringUserId);
        answer.addResponse(question1);
        answer.addResponse(question2);
        when(mockPersistence.getAnswer(answeringUserId, formId)).thenReturn(answer);

        // Mock AnswerToQuestion entries
        when(mockPersistence.getAnswerToQuestion(answeringUserId, formId, question1))
                .thenReturn(new AnswerNumericalQuestion(answeringUserId, formId, question1, 5));

        when(mockPersistence.getAnswerToQuestion(answeringUserId, formId, question2))
                .thenReturn(new AnswerNumericalQuestion(answeringUserId, formId, question2, 10));

        // Mock Questions
        when(mockPersistence.getQuestion(formId, question1))
                .thenReturn(new NumericalQuestion(question1, "Q1", false, 0, 100));

        when(mockPersistence.getQuestion(formId, question2))
                .thenReturn(new NumericalQuestion(question2, "Q2", false, 0, 100));

        // Mock void deletes
        mockVoidDeletes();

        // Execute
        ctrlDomain.deleteForm(formId);

        // Verify persistence calls
        verify(mockPersistence).deleteForm(formId);
        verify(mockPersistence).deleteQuestion(formId, question1);
        verify(mockPersistence).deleteQuestion(formId, question2);
        verify(mockPersistence).deleteAnswer(answeringUserId, formId);
        verify(mockPersistence).deleteAnalyze(formId, analyzeId);

        // Verify domain state updates
        assertFalse(owner.getForms().contains(formId));
        assertFalse(answeringUser.getAnswers().contains(formId));
        assertFalse(form.getAnswers().contains(answeringUserId));
    }

    @Test
    public void testExistsUserDelegates() {
        when(mockPersistence.existsUser(5)).thenReturn(true);

        assertTrue(ctrlDomain.existsUser(5));
        verify(mockPersistence).existsUser(5);
    }

    @Test
    public void testExistsFormDelegates() {
        when(mockPersistence.existsForm(10)).thenReturn(true);

        assertTrue(ctrlDomain.existsForm(10));
        verify(mockPersistence).existsForm(10);
    }

    @Test
    public void testExistsQuestionDelegates() {
        when(mockPersistence.existsQuestion(10, 3)).thenReturn(true);

        assertTrue(ctrlDomain.existsQuestion(10, 3));
        verify(mockPersistence).existsQuestion(10, 3);
    }

    @Test
    public void testExistsAnswerDelegates() {
        when(mockPersistence.existsAnswer(7, 10)).thenReturn(true);

        assertTrue(ctrlDomain.existsAnswer(7, 10));
        verify(mockPersistence).existsAnswer(7, 10);
    }

    @Test
    public void testExistsAnswerToQuestionDelegates() {
        when(mockPersistence.existsAnswerToQuestion(7, 10, 1)).thenReturn(true);

        assertTrue(ctrlDomain.existsAnswerToQuestion(7, 10, 1));
        verify(mockPersistence).existsAnswerToQuestion(7, 10, 1);
    }

    @Test
    public void testExistsAnalyzeDelegates() {
        when(mockPersistence.existsAnalyze(10, 50)).thenReturn(true);

        assertTrue(ctrlDomain.existsAnalyze(10, 50));
        verify(mockPersistence).existsAnalyze(10, 50);
    }

    @Test
    public void testGetContestableForms() throws Exception {
        RegisteredUser ru = new RegisteredUser(3, "anna", "pass1234", "a@mail.com");
        when(mockPersistence.getUser(3)).thenReturn(ru);

        // Forms: 10 (published, other owner), 11 (unpublished), 12 (owned by user)
        Form f1 = new Form(10, "A", 99); f1.publish();
        Form f2 = new Form(11, "B", 99);
        Form f3 = new Form(12, "C", 3);

        when(mockPersistence.getForm(10)).thenReturn(f1);
        when(mockPersistence.getForm(11)).thenReturn(f2);
        when(mockPersistence.getForm(12)).thenReturn(f3);

        when(mockPersistence.getAllForms()).thenReturn(
                new ArrayList<>(Arrays.asList(10, 11, 12))
        );

        ArrayList<Integer> result = ctrlDomain.getContestableForms(3);

        assertEquals(Arrays.asList(10), result);
    }

    @Test
    public void testGetAnswerDistances() throws Exception {
        Form f = new Form(10, "Survey", 3);
        f.addAnswer(7);

        Answer a = new Answer(10, 7);
        a.addResponse(1);

        AnswerToQuestion atq = new AnswerNumericalQuestion(7, 10, 1, 5);

        when(mockPersistence.getForm(10)).thenReturn(f);
        when(mockPersistence.getAnswer(7, 10)).thenReturn(a);
        when(mockPersistence.getAnswerToQuestion(7, 10, 1)).thenReturn(atq);

        double[][] expected = new double[][]{{0.0}};
        when(mockTransform.getAnswersDistanceMatrix(any())).thenReturn(expected);

        double[][] result = ctrlDomain.getAnswerDistances(10);

        assertEquals(0.0, result[0][0], 0.0001);
    }

    @Test
    public void testGetAnswerCoordinates() throws Exception {
        Form f = new Form(10, "Survey", 3);
        f.addAnswer(7);

        Answer a = new Answer(10, 7);
        a.addResponse(1);

        AnswerToQuestion atq = new AnswerNumericalQuestion(7, 10, 1, 5);

        when(mockPersistence.getForm(10)).thenReturn(f);
        when(mockPersistence.getAnswer(7, 10)).thenReturn(a);
        when(mockPersistence.getAnswerToQuestion(7, 10, 1)).thenReturn(atq);

        ArrayList<ArrayList<Double>> coords =
                new ArrayList<>(Collections.singletonList(
                        new ArrayList<>(Arrays.asList(0.1, 0.2))
                ));

        when(mockTransform.getAnswersCoordinates(any())).thenReturn(coords);

        ArrayList<ArrayList<Double>> result = ctrlDomain.getAnswerCoordinates(10);

        assertEquals(Double.valueOf(0.1), result.get(0).get(0));
        assertEquals(Double.valueOf(0.2), result.get(0).get(1));
    }

    @Test
    public void testGetAnalysisCentroids() throws Exception {
        Analyze analysis = mock(Analyze.class);
        AnalysisResult result = mock(AnalysisResult.class);

        ArrayList<ArrayList<AnswerToQuestion>> centroids = new ArrayList<>();
        centroids.add(new ArrayList<>());

        when(mockPersistence.getAnalyze(10, 50)).thenReturn(analysis);
        when(analysis.getResult()).thenReturn(result);
        when(result.getCentroids()).thenReturn(centroids);

        ArrayList<ArrayList<Object>> transformed = new ArrayList<>();
        transformed.add(new ArrayList<>(Arrays.asList("X")));

        when(mockTransform.transformAnswers(centroids)).thenReturn(transformed);

        ArrayList<ArrayList<Object>> out = ctrlDomain.getAnalysisCentroids(50, 10);

        assertEquals("X", out.get(0).get(0));
    }

    @Test
    public void testModifyMultipleChoiceQuestionSuccess() throws Exception {
        RegisteredUser ru = new RegisteredUser(3, "anna", "pass1234", "a@mail.com");
        Form f = new Form(10, "Survey", 3);

        ArrayList<String> oldOptions = new ArrayList<>(Arrays.asList("A", "B"));
        MultipleChoiceQuestion mcq =
                new MultipleChoiceQuestion(1, "Pick", false, oldOptions, 1, 2);
        mcq.setIdForm(10);

        when(mockPersistence.getUser(3)).thenReturn(ru);
        when(mockPersistence.getForm(10)).thenReturn(f);
        when(mockPersistence.getQuestion(10, 1)).thenReturn(mcq);

        ctrlDomain.loginUser(3, "pass1234");

        ArrayList<String> newOptions = new ArrayList<>(Arrays.asList("X", "Y", "Z"));
        ctrlDomain.modifyMultipleChoiceQuestion(10, 1, "NewPick", true, newOptions, 2, 3);

        assertEquals("NewPick", mcq.getStatement());
        assertTrue(mcq.isOptional());
        assertEquals(newOptions, mcq.getOptions());
        assertEquals(2, mcq.getMinSelections());
        assertEquals(3, mcq.getMaxSelections());
    }

    @Test
    public void testModifySingleChoiceQuestionSuccess() throws Exception {
        RegisteredUser ru = new RegisteredUser(3, "anna", "pass1234", "a@mail.com");
        Form f = new Form(10, "Survey", 3);

        ArrayList<String> oldOptions = new ArrayList<>(Arrays.asList("A", "B"));
        MultipleChoiceQuestion mcq =
                new MultipleChoiceQuestion(1, "Pick", false, oldOptions, 1, 1);
        mcq.setIdForm(10);

        when(mockPersistence.getUser(3)).thenReturn(ru);
        when(mockPersistence.getForm(10)).thenReturn(f);
        when(mockPersistence.getQuestion(10, 1)).thenReturn(mcq);

        ctrlDomain.loginUser(3, "pass1234");

        ArrayList<String> newOptions = new ArrayList<>(Arrays.asList("X", "Y"));
        ctrlDomain.modifySingleChoiceQuestion(10, 1, "NewPick", true, newOptions);

        assertEquals("NewPick", mcq.getStatement());
        assertTrue(mcq.isOptional());
        assertEquals(newOptions, mcq.getOptions());
    }

    @Test
    public void testModifyOrderedSingleQuestionSuccess() throws Exception {
        RegisteredUser ru = new RegisteredUser(3, "anna", "pass1234", "a@mail.com");
        Form f = new Form(10, "Survey", 3);

        ArrayList<String> oldOptions = new ArrayList<>(Arrays.asList("A", "B"));
        OrderedSingleQuestion osq =
                new OrderedSingleQuestion(1, "Pick", false, oldOptions);
        osq.setIdForm(10);

        when(mockPersistence.getUser(3)).thenReturn(ru);
        when(mockPersistence.getForm(10)).thenReturn(f);
        when(mockPersistence.getQuestion(10, 1)).thenReturn(osq);

        ctrlDomain.loginUser(3, "pass1234");

        ArrayList<String> newOptions = new ArrayList<>(Arrays.asList("X", "Y", "Z"));
        ctrlDomain.modifyOrderedSingleQuestion(10, 1, "NewPick", true, newOptions);

        assertEquals("NewPick", osq.getStatement());
        assertTrue(osq.isOptional());
        assertEquals(newOptions, osq.getOptions());
    }

    @Test
    public void testModifyMultipleChoiceAnswerSuccess() throws Exception {
        AnswerMultipleChoiceQuestion amcq =
                new AnswerMultipleChoiceQuestion(7, 10, 1, new ArrayList<>(Arrays.asList(1, 2)));

        when(mockPersistence.getAnswerToQuestion(7, 10, 1)).thenReturn(amcq);

        RegisteredUser ru = new RegisteredUser(7, "john", "pass1234", "j@mail.com");
        when(mockPersistence.getUser(7)).thenReturn(ru);
        ctrlDomain.loginUser(7, "pass1234");

        ArrayList<Integer> newAns = new ArrayList<>(Arrays.asList(3, 4));
        ctrlDomain.modifyMultipleChoiceAnswer(7, 10, 1, newAns);

        assertEquals(newAns, amcq.getSelectedOptionIndexes());
    }

    @Test
    public void testModifyOrderedSingleAnswerSuccess() throws Exception {
        AnswerSingleChoiceQuestion ascq =
                new AnswerSingleChoiceQuestion(7, 10, 1, 2);

        when(mockPersistence.getAnswerToQuestion(7, 10, 1)).thenReturn(ascq);

        RegisteredUser ru = new RegisteredUser(7, "john", "pass1234", "j@mail.com");
        when(mockPersistence.getUser(7)).thenReturn(ru);
        ctrlDomain.loginUser(7, "pass1234");

        ctrlDomain.modifyOrderedSingleAnswer(7, 10, 1, 5);

        assertEquals(Integer.valueOf(5), ascq.getSelectedOptionIndex());
    }

    @Test
    public void testGetFreeAnswer() throws Exception {
        AnswerFreeQuestion afq =
                new AnswerFreeQuestion(7, 10, 1, "hello");

        when(mockPersistence.getAnswerToQuestion(7, 10, 1)).thenReturn(afq);

        RegisteredUser ru = new RegisteredUser(7, "john", "pass1234", "j@mail.com");
        when(mockPersistence.getUser(7)).thenReturn(ru);
        ctrlDomain.loginUser(7, "pass1234");

        String result = ctrlDomain.getFreeAnswer(10, 1);

        assertEquals("hello", result);
    }

    @Test
    public void testGetOrderedSingleAnswer() throws Exception {
        AnswerSingleChoiceQuestion ascq =
                new AnswerSingleChoiceQuestion(7, 10, 1, 3);

        when(mockPersistence.getAnswerToQuestion(7, 10, 1)).thenReturn(ascq);

        RegisteredUser ru = new RegisteredUser(7, "john", "pass1234", "j@mail.com");
        when(mockPersistence.getUser(7)).thenReturn(ru);
        ctrlDomain.loginUser(7, "pass1234");

        Integer result = ctrlDomain.getOrderedSingleAnswer(10, 1);

        assertEquals(Integer.valueOf(3), result);
    }

    @Test
    public void testGetFormsFromUser() throws Exception {
        RegisteredUser ru = new RegisteredUser(3, "anna", "pass1234", "a@mail.com");
        ru.addForm(10);
        ru.addForm(20);

        when(mockPersistence.getUser(3)).thenReturn(ru);

        ArrayList<Integer> forms = ctrlDomain.getFormsFromUser(3);

        assertEquals(Arrays.asList(10, 20), forms);
    }

    @Test
    public void testGetFormInfoWithStatus() throws Exception {
        Form f = new Form(10, "Survey", 3);
        f.publish();

        RegisteredUser ru = new RegisteredUser(3, "anna", "pass1234", "a@mail.com");

        when(mockPersistence.getForm(10)).thenReturn(f);
        when(mockPersistence.getUser(3)).thenReturn(ru);
        when(mockTransform.getFormBasicInfo(10))
                .thenReturn(new HashMap<String, Object>() {{
                    put("name", "Survey");
                }});

        Map<String, Object> info = ctrlDomain.getFormInfoWithStatus(10);

        assertEquals("Survey", info.get("name"));
        assertEquals(true, info.get("isPublished"));
        assertEquals("anna", info.get("owner"));
    }

    @Test
    public void testGetAnalysisName() throws Exception {
        Analyze analysis = mock(Analyze.class);
        when(mockPersistence.getAnalyze(10, 50)).thenReturn(analysis);
        when(analysis.getName()).thenReturn("ClusterTest");

        String name = ctrlDomain.getAnalysisName(50, 10);

        assertEquals("ClusterTest", name);
    }

    @Test
    public void testDeleteUserCascade() throws Exception {
        mockVoidDeletes();

        final int userId = 3;
        final int form10 = 10;
        final int form20 = 20;
        final int answer30 = 30;
        final int answer40 = 40;

        
        // USER BEING DELETED
        RegisteredUser ru = new RegisteredUser(userId, "anna", "pass1234", "a@mail.com");
        ru.addForm(form10);
        ru.addForm(form20);
        ru.addAnswer(answer30);
        ru.addAnswer(answer40);
        when(mockPersistence.getUser(userId)).thenReturn(ru);

        
        // OTHER USERS WHO ANSWERED FORMS 10 AND 20
        RegisteredUser user7 = new RegisteredUser(7, "u7", "pass1234", "u7@mail.com");
        user7.addAnswer(form10);
        when(mockPersistence.getUser(7)).thenReturn(user7);

        RegisteredUser user8 = new RegisteredUser(8, "u8", "pass1234", "u8@mail.com");
        user8.addAnswer(form20);
        when(mockPersistence.getUser(8)).thenReturn(user8);

        
        // FORMS OWNED BY USER 3
        Form f10 = new Form(form10, "Form10", userId);
        f10.addQuestion(1);
        f10.addQuestion(2);
        f10.addAnswer(7);
        f10.addAnalyze(100);

        Form f20 = new Form(form20, "Form20", userId);
        f20.addQuestion(3);
        f20.addAnswer(8);
        f20.addAnalyze(200);

        when(mockPersistence.getForm(form10)).thenReturn(f10);
        when(mockPersistence.getForm(form20)).thenReturn(f20);

        // Forms referenced by user 3’s own answers
        when(mockPersistence.getForm(answer30)).thenReturn(new Form(answer30, "AnswerForm30", userId));
        when(mockPersistence.getForm(answer40)).thenReturn(new Form(answer40, "AnswerForm40", userId));

        
        // USER 3's OWN ANSWERS
        Answer a30 = new Answer(answer30, userId);
        a30.addResponse(5);
        when(mockPersistence.getAnswer(userId, answer30)).thenReturn(a30);
        when(mockPersistence.getAnswerToQuestion(userId, answer30, 5))
                .thenReturn(new AnswerNumericalQuestion(userId, answer30, 5, 10));

        Answer a40 = new Answer(answer40, userId);
        a40.addResponse(6);
        when(mockPersistence.getAnswer(userId, answer40)).thenReturn(a40);
        when(mockPersistence.getAnswerToQuestion(userId, answer40, 6))
                .thenReturn(new AnswerNumericalQuestion(userId, answer40, 6, 20));

        
        // OTHER USERS' ANSWERS
        Answer a7 = new Answer(form10, 7);
        a7.addResponse(1);
        a7.addResponse(2);
        when(mockPersistence.getAnswer(7, form10)).thenReturn(a7);
        when(mockPersistence.getAnswerToQuestion(7, form10, 1))
                .thenReturn(new AnswerNumericalQuestion(7, form10, 1, 5));
        when(mockPersistence.getAnswerToQuestion(7, form10, 2))
                .thenReturn(new AnswerNumericalQuestion(7, form10, 2, 6));

        Answer a8 = new Answer(form20, 8);
        a8.addResponse(3);
        when(mockPersistence.getAnswer(8, form20)).thenReturn(a8);
        when(mockPersistence.getAnswerToQuestion(8, form20, 3))
                .thenReturn(new AnswerNumericalQuestion(8, form20, 3, 7));

        
        // QUESTIONS
        when(mockPersistence.getQuestion(form10, 1)).thenReturn(new NumericalQuestion());
        when(mockPersistence.getQuestion(form10, 2)).thenReturn(new FreeQuestion());
        when(mockPersistence.getQuestion(form20, 3)).thenReturn(new MultipleChoiceQuestion());

        
        // EXECUTE DELETE
        ctrlDomain.deleteUser(userId);

        
        // VERIFY FORM DELETIONS
        verify(mockPersistence).deleteForm(form10);
        verify(mockPersistence).deleteForm(form20);

        
        // VERIFY QUESTION DELETIONS
        verify(mockPersistence).deleteQuestion(form10, 1);
        verify(mockPersistence).deleteQuestion(form10, 2);
        verify(mockPersistence).deleteQuestion(form20, 3);

        
        // VERIFY ANSWER-TO-QUESTION DELETIONS
        verify(mockPersistence).deleteAnswerToQuestion(7, form10, 1);
        verify(mockPersistence).deleteAnswerToQuestion(7, form10, 2);
        verify(mockPersistence).deleteAnswerToQuestion(8, form20, 3);

        
        // VERIFY ANSWER DELETIONS
        verify(mockPersistence).deleteAnswer(7, form10);
        verify(mockPersistence).deleteAnswer(8, form20);
        verify(mockPersistence).deleteAnswer(userId, answer30);
        verify(mockPersistence).deleteAnswer(userId, answer40);

        
        // VERIFY ANALYSIS DELETIONS
        verify(mockPersistence).deleteAnalyze(form10, 100);
        verify(mockPersistence).deleteAnalyze(form20, 200);

        
        // VERIFY USER DELETION
        verify(mockPersistence).deleteUser(userId);
    }
}
