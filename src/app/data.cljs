(ns app.data
  (:require [reagent.core :as r]
            [clojure.edn :as edn]))


(defn import-export []
  (let [show? (r/atom false)
        input (r/atom "")]
    (fn [flights]
      [:div
       [:h2 "Import/Export"]
       [:button {:on-click #(swap! show? not)}
        (if @show? "Hide" "Show")]
       (when @show?
         [:div
          [:h3 "Export"]
          [:p "Copy the content below to your friend"]
          [:p.export (pr-str @flights)]
          [:h3 "Import"]
          [:p "Paste exported data below and press the button"]
          [:textarea {:on-change #(reset! input (.-value (.-target %)))}]
          [:button {:on-click #(reset! flights (edn/read-string @input))} "Import"]])])))
