
import mongoose, { Schema } from "mongoose";

const itemSchema = new Schema({
    name:{
        type:String,
        required:true
    },
    description:{
        type:String,
        required:true
    },
    category: {
        type: String,
        // enum: ["Electronics", "Furniture", "Vehicles", "Tools", "Clothing", "Others"],
        // required: true
    },
    owner: {
        type: mongoose.Schema.Types.ObjectId,
        ref: "User",
        // required: true
    },
    price:{
        type:Number,
        required:true
    },
    available: {
        type: Boolean,
        default: true
    },
    images: {
        type: [String], // Array of image URLs
        default: []
    }

},{timestamps:true})

export const Item = mongoose.model("Item",itemSchema)
