(ns app.flights
  (:require [clojure.string :as str]))

(defn flights-ui [flights send-charlie trigger-landed]
  [:div
   (let [in-stack (filter (comp number? last val) @flights)]
     [:div.stack
      [:h2 "Stack"]
      [:p (str "Total: " (count in-stack))]
      [:table
       [:thead
        [:tr
         [:th "Name"]
         [:th "Altitude"]
         [:th "Charlie"]]]
       [:tbody
        (map (fn [[k v]]
               [:tr {:key k}
                [:td k]
                [:td (str/join "->" (filter number? (take-last 2 v)))]
                [:td [:button {:on-click #(send-charlie k)}
                      "Sent charlie"]]])
             (sort-by (comp last val) in-stack))]]])
   (let [on-approach (filter (comp #(= :charlie %) last val) @flights)]
     [:div.approach
      [:h2 "On approach"]
      [:p (str "Total: " (count on-approach))]
      [:table
       [:thead [:tr
                [:th "Name"]
                [:th "Land"]]]
       [:tbody
        (map (fn [[k _]]
               [:tr {:key k}
                [:td k]
                [:td [:button {:on-click #(trigger-landed k)}
                      "Landed"]]])
             on-approach)]]])])

