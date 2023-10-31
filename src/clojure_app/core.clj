(ns clojure-app.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (def Fielinvoice (clojure.edn/read-string (slurp "src/clojure_app/invoice.edn")))



  (defn isValidItem [item]
    (let [iva (some #(and (= (str (:tax/category %) ) ":iva") (= (str (:tax/rate %) ) "19")) (:taxable/taxes item ))
          retention (some #(and (= (str (:retention/category %) ) ":ret_fuente") (= (str (:retention/rate %) ) "1")) (:retentionable/retentions item ))]
      (not= iva retention))
    )


  "### Problem 1 Thread-last Operator ->>"
  (defn getItemsInvoice [facture]
    (->>
      (:invoice/items facture)
      (filter isValidItem)
      (map partial)
      )
    )

  (println "## Problem 1 items result:")
  (println (getItemsInvoice Fielinvoice))
)
