import { Item } from "../models/item.model.js";
import { uploadOnCloudinary } from "../utils/cloudinary.js";
import { asyncHandler } from "../utils/asyncHandler.js";
import { ApiError } from "../utils/ApiError.js";
export const createItem = asyncHandler(async (req, res) => {
        const { name, description, category, price } = req.body;
        console.log(req.body)
        const owner = req.user?._id;
        console.log(owner)

        if (!name || !description || !category || !price) {
            throw new ApiError(400, "All fields are required");
        }

        // Validate category
        const allowedCategories = ["Electronics", "Furniture", "Vehicles", "Tools", "Clothing", "Others"];
        if (!allowedCategories.includes(category)) {
            throw new ApiError(400, "Invalid category value");
        }

        if (!req.files || req.files.length === 0) {
            throw new ApiError(400, "At least one image is required");
        }


        const path = req.files?.images[0]?.path;
        const result = await uploadOnCloudinary(path);
        const imageUrls=result.url

        console.log(imageUrls)

        // Ensure images are uploaded successfully
        if (imageUrls.length === 0) {
            throw new ApiError(400, "Image upload failed");
        }

        // Create new item in MongoDB
        const newItem = new Item({
            name,
            description,
            price,
            images: imageUrls, 
        });

        await newItem.save();

        return res.status(201).json({ success: true, item: newItem, message: "Item uploaded successfully" });

});
