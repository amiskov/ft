(ns gaps-editor.core
   (:require [reagent.dom :as rdom]))

 (defn app []
   [:div "Hello from Gaps Editor!"])

 (defn ^:export main []
   (rdom/render [app] (js/document.getElementById "app")))