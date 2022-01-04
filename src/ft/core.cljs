(ns ft.core
  (:require [reagent.dom :as rdom]
            [ft.gaps-editor :refer [editor repr parse-phrase editor-content]]))

(defn app []
  [:div (editor)
   (repr)])

(defn ^:export main []
  (rdom/render [app] (js/document.getElementById "app"))
  (parse-phrase "When I [was walking](walked, walk) to the office, a guy on a Segway almost [knocked me down](was knocking me down, knock me down)."))
  ;(parse-phrase "London is [the](a) capital and largest city of England and the United Kingdom."))
