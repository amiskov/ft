(ns ft.core
  (:require [reagent.dom :as rdom]
            [ft.gaps-editor :refer [editor render-phrase parse-phrase exercise]]))

(defn app []
  [:div (editor)
   (render-phrase)])

(defn ^:export main []
  (rdom/render [app] (js/document.getElementById "app"))
  (parse-phrase "When I {*was walking*; walked; walk} to the office, a guy on a Segway almost {*knocked me down*; was knocking me down; knock me down}."))
