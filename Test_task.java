import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    private final List<com.resleteam.netufosbackend.util.DocumentManager.Document> documents = new ArrayList<>();
    private int counter = 0;

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public com.resleteam.netufosbackend.util.DocumentManager.Document save(com.resleteam.netufosbackend.util.DocumentManager.Document document) {
        if(document.getId() == null || document.getId().isEmpty()) {
            document.setId(String.valueOf(++counter));
            documents.add(document);
        } else {
            documents.replaceAll(savedDoc -> savedDoc.getId().equals(document.getId()) ? document : savedDoc);
        }
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<com.resleteam.netufosbackend.util.DocumentManager.Document> search(com.resleteam.netufosbackend.util.DocumentManager.SearchRequest request) {
        return documents.stream()
                .filter(doc -> requestMatches(request, doc))
                .toList();
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<com.resleteam.netufosbackend.util.DocumentManager.Document> findById(String id) {
        return documents.stream()
                .filter(savedDoc -> savedDoc.getId().equals(id))
                .findFirst();
    }

    private boolean requestMatches(com.resleteam.netufosbackend.util.DocumentManager.SearchRequest request, com.resleteam.netufosbackend.util.DocumentManager.Document document) {
        if(request.getTitlePrefixes() != null && !request.getTitlePrefixes().isEmpty()) {
            boolean titleMatches = request.getTitlePrefixes().stream()
                    .anyMatch(prefix -> document.getTitle() != null && document.getTitle().startsWith(prefix));
            if (!titleMatches) return false;
        }

        if(request.getContainsContents() != null && !request.getContainsContents().isEmpty()) {
            boolean contentMatches = request.getContainsContents().stream()
                    .anyMatch(content -> document.getContent() != null && document.getContent().contains(content));
            if (!contentMatches) return false;
        }

        if(request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
            boolean authorMatches = request.getAuthorIds().stream()
                    .anyMatch(authorId -> document.getAuthor() != null && authorId.equals(document.getAuthor().getId()));
            if (!authorMatches) return false;
        }

        if (request.getCreatedFrom() != null && document.getCreated().isBefore(request.getCreatedFrom())) {
            return false;
        }

        if (request.getCreatedTo() != null && document.getCreated().isAfter(request.getCreatedTo())) {
            return false;
        }

        return true;
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