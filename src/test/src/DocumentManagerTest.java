package test.src;

import main.DocumentManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DocumentManagerTest {

    private DocumentManager documentManager;

    @BeforeEach
    void setUp() {
        documentManager = new DocumentManager();
    }

    @Test
    void testSaveNewDocument() {
        DocumentManager.Author author = DocumentManager.Author.builder().id("author1").name("Taras Shevchenko").build();
        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Test Document")
                .content("Some test text")
                .author(author)
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);

        assertNotNull(savedDocument.getId());
        assertNotNull(savedDocument.getCreated());
        assertEquals("Test Document", savedDocument.getTitle());
        assertEquals("Some test text", savedDocument.getContent());
        assertEquals("Taras Shevchenko", savedDocument.getAuthor().getName());
    }

    @Test
    void testSaveExistingDocument() {
        DocumentManager.Author author = DocumentManager.Author.builder().id("author2").name("Ivan Franko").build();
        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("First Document")
                .content("Content of the first document")
                .author(author)
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);

        Instant createdTime = savedDocument.getCreated();

        savedDocument.setContent("Updated content of the first document");
        DocumentManager.Document updatedDocument = documentManager.save(savedDocument);

        assertEquals(savedDocument.getId(), updatedDocument.getId());
        assertEquals("Updated content of the first document", updatedDocument.getContent());
        assertEquals(createdTime, updatedDocument.getCreated());
    }

    @Test
    void testSearchDocumentsByTitlePrefix() {
        DocumentManager.Author author = DocumentManager.Author.builder().id("author3").name("Lina Kostenko").build();
        documentManager.save(DocumentManager.Document.builder()
                .title("Document to test prefix search")
                .content("Content of main document")
                .author(author)
                .build());
        documentManager.save(DocumentManager.Document.builder()
                .title("Another Document")
                .content("Content of another document")
                .author(author)
                .build());

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Doc"))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);

        assertEquals(1, results.size());
        assertEquals("Document to test prefix search", results.get(0).getTitle());
    }

    @Test
    void testSearchDocumentsByContent() {
        DocumentManager.Author author = DocumentManager.Author.builder().id("author4").name("Petro Mohyla").build();
        documentManager.save(DocumentManager.Document.builder()
                .title("First Document")
                .content("This is some unique content")
                .author(author)
                .build());
        documentManager.save(DocumentManager.Document.builder()
                .title("Second Document")
                .content("Another content here")
                .author(author)
                .build());

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .containsContents(List.of("unique"))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);

        assertEquals(1, results.size());
        assertEquals("First Document", results.get(0).getTitle());
    }

    @Test
    void testFindDocumentById() {
        DocumentManager.Author author = DocumentManager.Author.builder().id("author5").name("Panas Myrnyi").build();
        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Searchable Document")
                .content("Searchable content")
                .author(author)
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);
        String documentId = savedDocument.getId();

        Optional<DocumentManager.Document> foundDocument = documentManager.findById(documentId);

        assertTrue(foundDocument.isPresent());
        assertEquals("Searchable Document", foundDocument.get().getTitle());
        assertEquals("Searchable content", foundDocument.get().getContent());
    }

    @Test
    void testFindDocumentById_NotFound() {
        Optional<DocumentManager.Document> foundDocument = documentManager.findById("non-existent-id");

        assertFalse(foundDocument.isPresent());
    }

    @Test
    void testSearchDocumentsWithNoCriteria() {
        DocumentManager.Author author = DocumentManager.Author.builder().id("author6").name("Dave").build();
        documentManager.save(DocumentManager.Document.builder()
                .title("Document A")
                .content("Content A")
                .author(author)
                .build());
        documentManager.save(DocumentManager.Document.builder()
                .title("Document B")
                .content("Content B")
                .author(author)
                .build());

        List<DocumentManager.Document> results = documentManager.search(null);

        assertEquals(2, results.size());
    }
}
