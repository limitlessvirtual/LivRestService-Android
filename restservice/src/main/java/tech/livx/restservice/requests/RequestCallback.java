package tech.livx.restservice.requests;

/**
 * Description of class
 * Bugs: none known
 *
 * @author Mitch, LivX : livx.tech
 * @version 1.0
 */
public interface RequestCallback {
    void onComplete(long requestId, int code, RestRequest request);
    void onFailed(long requestId, RestRequest request);
}
