package com.tfdev.inventorymanagement.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.tfdev.inventorymanagement.data.entity.Customer
import com.tfdev.inventorymanagement.databinding.DialogCustomerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerDialogFragment : DialogFragment() {
    private var _binding: DialogCustomerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CustomerViewModel by viewModels()
    private var customer: Customer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customer = arguments?.getParcelable(ARG_CUSTOMER)
        setStyle(STYLE_NORMAL, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCustomerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupClickListeners()
    }

    private fun setupViews() {
        binding.apply {
            dialogTitle.text = if (customer == null) "Müşteri Ekle" else "Müşteri Düzenle"
            btnSave.text = if (customer == null) "Ekle" else "Güncelle"

            customer?.let { customer ->
                etCustomerName.setText(customer.name)
                etCustomerAddress.setText(customer.address)
                etCustomerCity.setText(customer.city)
                etCustomerPhone.setText(customer.phoneNumber)
                etCustomerMail.setText(customer.email)
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            saveCustomer()
        }
    }

    private fun saveCustomer() {
        val name = binding.etCustomerName.text.toString()
        val address = binding.etCustomerAddress.text.toString()
        val city = binding.etCustomerCity.text.toString()
        val phone = binding.etCustomerPhone.text.toString()
        val email = binding.etCustomerMail.text.toString()

        if (validateInputs(name, address, city, phone, email)) {
            val newCustomer = Customer(
                customerId = customer?.customerId ?: 0,
                name = name,
                address = address,
                city = city,
                phoneNumber = phone,
                email = email
            )

            if (customer == null) {
                viewModel.addCustomer(newCustomer)
            } else {
                viewModel.updateCustomer(newCustomer)
            }
            dismiss()
        }
    }

    private fun validateInputs(
        name: String,
        address: String,
        city: String,
        phone: String,
        email: String
    ): Boolean {
        var isValid = true

        if (name.isBlank()) {
            binding.etCustomerName.error = "Ad Soyad gerekli"
            isValid = false
        }
        if (address.isBlank()) {
            binding.etCustomerAddress.error = "Adres gerekli"
            isValid = false
        }
        if (city.isBlank()) {
            binding.etCustomerCity.error = "Şehir gerekli"
            isValid = false
        }
        if (phone.isBlank()) {
            binding.etCustomerPhone.error = "Telefon gerekli"
            isValid = false
        }
        if (email.isBlank()) {
            binding.etCustomerMail.error = "E-posta gerekli"
            isValid = false
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CUSTOMER = "customer"

        fun newInstance(customer: Customer?) = CustomerDialogFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_CUSTOMER, customer)
            }
        }
    }
} 