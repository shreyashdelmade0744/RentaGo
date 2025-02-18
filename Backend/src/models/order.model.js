import mongoose, { Schema } from "mongoose";

const orderSchema = new Schema({
    item: {
        type: mongoose.Schema.Types.ObjectId,
        ref: "Item",
        required: true
    },
    renter: {
        type: mongoose.Schema.Types.ObjectId,
        ref: "User",
        required: true
    },
    owner: {
        type: mongoose.Schema.Types.ObjectId,
        ref: "User",
        required: true
    },
    startDate: {
        type: Date,
        required: true
    },
    endDate: {
        type: Date,
        required: true
    },
    totalPrice: {
        type: Number,
        required: true,
        min: 0
    },
    paymentStatus: {
        type: String,
        enum: ["Pending", "Completed", "Failed", "Refunded"],
        default: "Pending"
    },
    status: {
        type: String,
        enum: ["Requested", "Accepted", "Rejected", "Cancelled", "Completed"],
        default: "Requested"
    }
},{timestamps:true})

export const Order = mongoose.model("Order",orderSchema)
