(ns app.app
  (:require [reagent.core :as r]
            [app.flights :as f]))

(defn stack-flight [existing name]
  (let [last-events (map (comp last val) existing)
        numbers (filter number? last-events)
        highest (apply max numbers)
        altitude (if highest (+ highest 1000) 2000)]
    (if (existing name)
      (conj (existing name) altitude)
      [altitude])))


(defn add-flight-form [add-flight-fn flights]
  (let [name (r/atom "")]
    (fn []
      [:div
       [:input {:type "text"
                :placeholder "Name"
                :value @name
                :on-change #(reset! name (.. % -target -value))}]
       [:button {:on-click #(do (add-flight-fn @name)
                                (reset! name ""))} "Add"]
       [:br]
       [:select {:on-change #(reset! name (.. % -target -value))}
        [:option {:value ""} "-- select --"]
        (map
         (fn [[k _]]
           [:option {:key k} k])
         (filter (comp not number? last val) @flights))]])))

(def flights (r/atom {}))

(defn send-charlie [name]
  (swap! flights (fn [old]
                   (into {}
                         (map (fn [[k v]]
                                [k (if (number? (last v))
                                     (conj v (- (last v) 1000))
                                     v)])
                              (update old name conj :charlie))))))

(defn set-landed [name]
  (swap! flights update name conj :landed))

(defn add-flight [name]
  (let [info (stack-flight @flights name)]
    (swap! flights assoc name info)))

(defn app []
  [:div
   [:h1 "Marshal"]
   [add-flight-form add-flight flights]
   [f/flights-ui flights send-charlie set-landed]])
