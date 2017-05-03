package tech.livx.restservice.requests;

/**
 * Description of class
 * <p/>
 * Bugs: none known
 *
 * @author Mitch, LivX : livx.tech
 * @version 1.0
 * @date 2017/04/13
 */
public interface RequestCallback {
    void onComplete(long requestId, int code, RestRequest request);
    void onFailed(long requestId, RestRequest request);
}
