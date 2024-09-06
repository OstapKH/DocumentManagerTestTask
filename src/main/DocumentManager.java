package main;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    private final Map<String, Document> documentStorage = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.getId() == null || !documentStorage.containsKey(document.getId())) {
            String newId = UUID.randomUUID().toString();
            document.setId(newId);
            document.setCreated(Instant.now());
        } else {
            Document existingDoc = documentStorage.get(document.getId());
            document.setCreated(existingDoc.getCreated());
        }

        documentStorage.put(document.getId(), document);
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        if (request == null) {
            return new ArrayList<>(documentStorage.values());
        }

        return documentStorage.values().stream()
                .filter(document -> matches(document, request))
                .toList();
    }

    private boolean matches(Document document, SearchRequest request) {
        if (request.getTitlePrefixes() != null && !request.getTitlePrefixes().isEmpty() &&
                request.getTitlePrefixes().stream().noneMatch(document.getTitle()::startsWith)) {
            return false;
        }

        if (request.getContainsContents() != null && !request.getContainsContents().isEmpty() &&
                request.getContainsContents().stream().noneMatch(document.getContent()::contains)) {
            return false;
        }

        if (request.getAuthorIds() != null && !request.getAuthorIds().isEmpty() &&
                request.getAuthorIds().stream().noneMatch(authorId -> authorId.equals(document.getAuthor().getId()))) {
            return false;
        }

        if (request.getCreatedFrom() != null && document.getCreated().isBefore(request.getCreatedFrom())) {
            return false;
        }

        if (request.getCreatedTo() != null && document.getCreated().isAfter(request.getCreatedTo())) {
            return false;
        }
        return true;
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(documentStorage.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}