(ns clojure_app.invoice-item
  (:require [clojure.test :refer :all])
  )

(defn- discount-factor [{:invoice-item/keys [discount-rate]
                         :or                {discount-rate 0}}]
  (- 1 (/ discount-rate 100.0)))

(defn subtotal
  [{:invoice-item/keys [precise-quantity precise-price discount-rate]
    :as                item
    :or                {discount-rate 0}}]
  (print (* precise-price precise-quantity (discount-factor item)))
  (* precise-price precise-quantity (discount-factor item)))

(deftest test-subtotal-with-discount
  (is (= 95.0  (subtotal {:invoice-item/precise-quantity 10 :invoice-item/precise-price 10 :invoice-item/discount-rate 5}))))

(deftest test-subtotal-without-discount
  (is (= 100.0  (subtotal {:invoice-item/precise-quantity 10 :invoice-item/precise-price 10}))))

(deftest test-subtotal-function
  (let [quantity 10  price 10 discount 0]
    (is (= (double (* quantity price)) (subtotal {:invoice-item/precise-quantity quantity :invoice-item/precise-price price :invoice-item/discount-rate discount})))))


(deftest test-subtotal-with-discount-function
  (let [quantity 3  price 6 discount 50]
    (is (= (double (* quantity price (- 1 (/ discount 100.0)))) (subtotal {:invoice-item/precise-quantity quantity :invoice-item/precise-price price :invoice-item/discount-rate discount})))))

(deftest test-subtotal-with-discount-negative-function
  (let [quantity -10  price 10 discount -3]
    (is (= (double (* quantity price (- 1 (/ discount 100.0)))) (subtotal {:invoice-item/precise-quantity quantity :invoice-item/precise-price price :invoice-item/discount-rate discount})))))



(run-tests)
