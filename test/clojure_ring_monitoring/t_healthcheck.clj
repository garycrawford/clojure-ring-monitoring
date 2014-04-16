(ns clojure-ring-monitoring.t-healthcheck
  (:use midje.sweet
        ring.mock.request)
  (:import java.lang.Exception)
  (:require [clojure-ring-monitoring.healthcheck :as health]))

(defn get-response
  ""
  [target-url checkfn request-url]
  ((health/healthcheck-middleware (fn [request] target-url { :response "not handled" }) target-url {:simple-healthcheck checkfn}) (request :get request-url)))

(fact "healthcheck returns the right status code"
        (:status (get-response "/healthcheck" (fn [] true) "/healthcheck")) => 200
        (:status (get-response "/healthcheck" (fn [] false) "/healthcheck")) => 500
        (:status (get-response "/healthcheck" (fn [] "simple string") "/healthcheck")) => 200
        (:status (get-response "/healthcheck" (fn [] nil) "/healthcheck")) => 500
        (:status (get-response "/healthcheck" (fn [] (.throw (Exception. "whoopsie"))) "/healthcheck")) => 500)

(fact "healthcheck returns string message on both success and failure"
        (:body (get-response "/healthcheck" (fn [] "This really works!") "/healthcheck")) => (contains "success! Result: This really works!")
        (:body (get-response "/healthcheck" (fn [] (.throw (Exception. "whoopsie"))) "/healthcheck")) => (contains "failed! Result: java.lang.IllegalArgumentException: No matching field found: throw for class java.lang.Exception"))

(fact "healthcheck urls can be changed"
      (:status (get-response "/admin/healthcheck" (fn [] true) "/admin/healthcheck")) => 200
      (:response (get-response "/admin/healthcheck" (fn [] true) "/admin")) => "not handled")
