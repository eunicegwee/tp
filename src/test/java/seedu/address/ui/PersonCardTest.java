package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    private static final AtomicBoolean IS_JAVAFX_STARTED = new AtomicBoolean(false);
    private static volatile boolean isJavaFxAvailable = true;

    @BeforeAll
    public static void setUpClass() throws InterruptedException {
        if (IS_JAVAFX_STARTED.compareAndSet(false, true)) {
            CountDownLatch latch = new CountDownLatch(1);
            try {
                Platform.startup(latch::countDown);
                latch.await();
            } catch (UnsupportedOperationException e) {
                isJavaFxAvailable = false;
            }
        }
    }

    @Test
    public void constructor_starredPerson_displaysSortedTagsAndStar() throws Exception {
        assertJavaFxAvailable();
        Person person = new PersonBuilder()
                .withName("Zara")
                .withTags("beta", "alpha")
                .withStar(true)
                .build();

        PersonCard personCard = createPersonCard(person, 3);

        assertEquals("3", getPrivateField(personCard, "id", Label.class).getText());
        assertEquals("Zara", getPrivateField(personCard, "name", Label.class).getText());
        assertEquals("\u2605", getPrivateField(personCard, "starredIndicator", Label.class).getText());
        assertEquals(List.of("alpha", "beta"), getTagTexts(personCard));
    }

    @Test
    public void constructor_unstarredPerson_hidesStarIndicator() throws Exception {
        assertJavaFxAvailable();
        Person person = new PersonBuilder().withName("Yuki").build();

        PersonCard personCard = createPersonCard(person, 1);

        assertEquals("", getPrivateField(personCard, "starredIndicator", Label.class).getText());
    }

    private PersonCard createPersonCard(Person person, int displayedIndex) throws Exception {
        AtomicReference<PersonCard> personCardReference = new AtomicReference<>();
        runOnFxThread(() -> personCardReference.set(new PersonCard(person, displayedIndex)));
        return personCardReference.get();
    }

    private List<String> getTagTexts(PersonCard personCard) throws Exception {
        FlowPane tags = getPrivateField(personCard, "tags", FlowPane.class);
        return tags.getChildren().stream()
                .map(node -> ((Label) node).getText())
                .collect(Collectors.toList());
    }

    private void runOnFxThread(ThrowingRunnable action) throws Exception {
        AtomicReference<Throwable> thrown = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable throwable) {
                thrown.set(throwable);
            } finally {
                latch.countDown();
            }
        });
        latch.await();
        if (thrown.get() != null) {
            throw new RuntimeException(thrown.get());
        }
    }

    private <T> T getPrivateField(PersonCard personCard, String fieldName, Class<T> type) throws Exception {
        Field field = PersonCard.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return type.cast(field.get(personCard));
    }

    private void assertJavaFxAvailable() {
        Assumptions.assumeTrue(isJavaFxAvailable, "JavaFX toolkit is not available in this environment");
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
