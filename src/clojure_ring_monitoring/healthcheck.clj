(ns clojure-ring-monitoring.healthcheck
  (:use [ring.util.response :only [response status content-type]]
        [hiccup.core :only (html)]
        [hiccup.page :only (html5)]))

(defn- healthcheck-result [check]
  (try
    (let [check-result (check)]
      (if check-result
        [::success check-result]
        [::failure (if (nil? check-result) "result was nil" check-result)]))
    (catch Exception e
      [::failure e])))

(defn- all-succeeded? [healthcheck-results]
  (empty? (filter #(not (= ::success (first (second %)))) healthcheck-results)))

(defn- run-healthchecks [healthcheck-map]
  (into {} (map (fn [[name check]] [name (healthcheck-result check)]) healthcheck-map)))

(defn- result->html [[check-name [status result]]]
  (list [:dt.check_name (name check-name)]
        [:dd.check_result (if (= status ::success)
                            [:span.success "success"]
                            [:span.failure (str "failed! Result: "
                                                (.toString result))])]))

(defn- html-report [results]
  (html5 [:header [:title "Healthchecks"]]
         [:body
          [:section [:p "The following healthchecks have been registered:"]
           [:dl (map result->html results)]]]))

(defn- build-response
  ""
  [results]
  (-> (response (html-report results))
      (content-type "text/html")
      (status (if (all-succeeded? results) 200 500))))

(defn healthcheck-middleware [handler rel-url healthchecks]
  (fn [request]
    (if (= (:uri request) rel-url)
      (-> healthchecks run-healthchecks build-response)
      (handler request))))
