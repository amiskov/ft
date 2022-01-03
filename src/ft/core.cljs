(ns ft.core
  (:require [reagent.dom :as rdom]
            [ft.gaps-editor :refer [editor repr]]))

(defn app []
  [:div (editor)
   [:hr]
   (repr)])

(defn ^:export main []
  (rdom/render [app] (js/document.getElementById "app")))