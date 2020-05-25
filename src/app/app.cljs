(ns app.app
  (:require [reagent.core :as r]
            [app.flights :as f]
            [app.data :as d]))

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
      (let [submit (fn []
                     (add-flight-fn @name)
                     (reset! name ""))]
        [:div
         [:input {:type "text"
                  :placeholder "Name"
                  :value @name
                  :on-change #(reset! name (.. % -target -value))
                  :on-key-down #(when (= 13 (.-keyCode %))
                                  (submit))}]
         [:button {:on-click submit} "Add"]
         [:br]
         [:select {:on-change #(reset! name (.. % -target -value))}
          [:option {:value ""} "-- select --"]
          (map
           (fn [[k _]]
             [:option {:key k} k])
           (filter (comp not number? last val) @flights))]]))))

(def flights (r/atom {}))

(defn send-charlie [name]
  (swap! flights (fn [old]
                   (let [old-alt (last (old name))
                         with-charlie (update old name conj :charlie)]
                     (into {}
                           (map (fn [[k v]]
                                  [k (if (and (number? (last v))
                                              (> (last v) old-alt))
                                       (conj v (- (last v) 1000))
                                       v)])
                                with-charlie))))))

(defn set-landed [name]
  (swap! flights update name conj :landed))

(defn add-flight [name]
  (let [info (stack-flight @flights name)]
    (swap! flights assoc name info)))

(defn change-altitude [change name]
  (let [new (+ change (last (@flights name)))]
    (swap! flights update name conj new)))

(def altitude-up (partial change-altitude 1000))
(def altitude-down (partial change-altitude -1000))

(defn app []
  [:div
   [:h1 "Marshal"]
   [add-flight-form add-flight flights]
   [f/flights-ui flights send-charlie set-landed altitude-up altitude-down]
   [d/import-export flights]])
