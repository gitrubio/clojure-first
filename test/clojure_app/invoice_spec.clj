(ns clojure_app.invoice-spec
  (:require
    [clojure.spec.alpha :as s]
    [clojure.data.json :as json]
    [clj-time.format :as time-format]
    )

  )

(defn not-blank? [value] (-> value clojure.string/blank? not))
(defn non-empty-string? [x] (and (string? x) (not-blank? x)))

(s/def :customer/name non-empty-string?)
(s/def :customer/email non-empty-string?)
(s/def :invoice/customer (s/keys :req [:customer/name
                                       :customer/email]))

(s/def :tax/rate double?)
(s/def :tax/category #{:iva})
(s/def ::tax (s/keys :req [:tax/category
                           :tax/rate]))
(s/def :invoice-item/taxes (s/coll-of ::tax :kind vector? :min-count 1))

(s/def :invoice-item/price double?)
(s/def :invoice-item/quantity double?)
(s/def :invoice-item/sku non-empty-string?)

(s/def ::invoice-item
  (s/keys :req [:invoice-item/price
                :invoice-item/quantity
                :invoice-item/sku
                :invoice-item/taxes]))

(s/def :invoice/issue-date inst?)
(s/def :invoice/items (s/coll-of ::invoice-item :kind vector? :min-count 1))

(s/def ::invoice
  (s/keys :req [:invoice/issue-date
                :invoice/customer
                :invoice/items]))




(defn generateDate [stringDate] (time-format/parse  (time-format/formatter-local "DD/MM/YYYY") stringDate))
(defn vectorTaxes [array] (vec (map  #(-> {
                                         :tax/rate (double (get % "tax_rate")),
                                         :tax/category :iva,}) array)))
"main function"
(defn generateInvoice [json_name]
  ( let [ data (json/read-str (slurp (str "./" json_name ".json")))]
    {
     :invoice/issue-date (generateDate (get-in data ["invoice" "issue_date"])),
     :invoice/customer   {
                           :customer/name (get-in data ["invoice" "customer" "company_name"]),
                           :customer/email (get-in data ["invoice" "customer" "email"])
                           },
     :invoice/items      (vec (map #(-> {:invoice-item/price    (get % "price")
                                         :invoice-item/quantity (get % "quantity"),
                                         :invoice-item/sku      (get % "sku"),
                                         :invoice-item/taxes    (vectorTaxes (get % "taxes") )}) (get-in data ["invoice" "items"])))
     }
    ))




(def invoice (generateInvoice "invoice") )
(print "## Problem 2 => test result : ")

(print (s/valid? ::invoice invoice))