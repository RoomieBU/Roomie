import React, { useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import './SharedSupply.css';


const Inventory = () => {
  const [items, setItems] = useState([
    { id: 1, name: 'Toilet Paper', quantity: 6 },
    { id: 2, name: 'Soap', quantity: 3 },
    { id: 3, name: 'Paper Towels', quantity: 5 }
  ]);

    return (
      <div className="container py-5">
        <h1 className="text-center mb-4 inventory-title">Shared Supply Inventory</h1>

        <div className="list-group mb-4">
          {items.map((item) => (
            <div key={item.id} className="list-group-item d-flex justify-content-between align-items-center">
              <div>
                <strong>{item.name}</strong> <span className="text-muted">x{item.quantity}</span>
              </div>
              <div>
                <button className="btn btn-sm btn-outline-secondary me-2">Edit</button>
                <button className="btn btn-sm btn-outline-danger">Delete</button>
              </div>
            </div>
          ))}
        </div>

        <div className="d-flex justify-content-center">
          <button className="btn add-btn px-4 py-2">+ Add Item</button>
        </div>
      </div>
      );
  };

  export default Inventory;